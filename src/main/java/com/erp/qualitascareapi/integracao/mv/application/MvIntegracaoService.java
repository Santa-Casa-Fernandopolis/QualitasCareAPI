package com.erp.qualitascareapi.integracao.mv.application;

import com.erp.qualitascareapi.integracao.mv.api.dto.CirurgiaAgendadaDto;
import com.erp.qualitascareapi.integracao.mv.api.dto.SincronizacaoResultadoDto;
import com.erp.qualitascareapi.integracao.mv.domain.CirurgiaAgendada;
import com.erp.qualitascareapi.integracao.mv.enums.StatusCirurgiaMv;
import com.erp.qualitascareapi.integracao.mv.repo.CirurgiaAgendadaRepository;
import com.erp.qualitascareapi.integracao.mv.strategy.MvCirurgiaRaw;
import com.erp.qualitascareapi.integracao.mv.strategy.MvIntegracaoException;
import com.erp.qualitascareapi.integracao.mv.strategy.MvIntegracaoStrategy;
import com.erp.qualitascareapi.sistema.application.ConfiguracaoService;
import com.erp.qualitascareapi.sistema.enums.ModuloConfiguracao;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Orquestra a sincronização de cirurgias agendadas do Soul MV com a base local.
 *
 * <h3>Seleção de estratégia em runtime</h3>
 * O parâmetro {@code MV_INTEGRACAO_TIPO} em {@code sys_configuracoes} determina
 * qual implementação será usada:
 * <ul>
 *   <li>{@code API}         → {@link com.erp.qualitascareapi.integracao.mv.strategy.MvApiStrategy}</li>
 *   <li>{@code BANCO_DADOS} → {@link com.erp.qualitascareapi.integracao.mv.strategy.MvBancoDadosStrategy}</li>
 * </ul>
 * A troca ocorre em tempo de execução — sem restart.
 *
 * <h3>Controle de sync</h3>
 * O job só executa quando {@code MV_INTEGRACAO_ATIVA = true} em {@code sys_configuracoes}.
 * O intervalo e a antecedência de dias também vêm do banco de configurações.
 */
@Service
public class MvIntegracaoService {

    private static final Logger log = LoggerFactory.getLogger(MvIntegracaoService.class);

    private final ConfiguracaoService configuracaoService;
    private final CirurgiaAgendadaRepository repository;

    // Ambas as strategies injetadas — resolução feita em runtime
    private final MvIntegracaoStrategy mvApiStrategy;
    private final MvIntegracaoStrategy mvBancoDadosStrategy;

    public MvIntegracaoService(
            ConfiguracaoService configuracaoService,
            CirurgiaAgendadaRepository repository,
            @Qualifier("mv-api")   MvIntegracaoStrategy mvApiStrategy,
            @Qualifier("mv-banco") MvIntegracaoStrategy mvBancoDadosStrategy) {
        this.configuracaoService  = configuracaoService;
        this.repository           = repository;
        this.mvApiStrategy        = mvApiStrategy;
        this.mvBancoDadosStrategy = mvBancoDadosStrategy;
    }

    // ─── Sync (chamado pelo job agendado e pelo endpoint manual) ──────────────

    /**
     * Executa a sincronização de cirurgias agendadas.
     * Não lança exceção — erros são registrados e retornados no resultado.
     */
    @Transactional
    public SincronizacaoResultadoDto sincronizarCirurgias() {
        LocalDateTime inicio = LocalDateTime.now();

        boolean ativa = configuracaoService.getValorBoolean(ModuloConfiguracao.MV, "MV_INTEGRACAO_ATIVA", false);
        if (!ativa) {
            log.debug("[MV-SYNC] Integração desativada (MV_INTEGRACAO_ATIVA=false) — sync ignorado");
            return SincronizacaoResultadoDto.desativada(inicio);
        }

        Long tenantId = obterTenantId();
        if (tenantId == null) {
            log.warn("[MV-SYNC] MV_TENANT_ID não configurado — sync abortado");
            return SincronizacaoResultadoDto.falha(inicio, resolverNomeEstrategia(),
                    "MV_TENANT_ID não configurado em sys_configuracoes (módulo MV)");
        }

        int diasAntecedencia = configuracaoService.getValorInt(ModuloConfiguracao.MV, "MV_SYNC_DIAS_ANTECEDENCIA").orElse(7);
        LocalDate dataInicio = LocalDate.now();
        LocalDate dataFim    = dataInicio.plusDays(diasAntecedencia);

        String estrategia = resolverNomeEstrategia();
        log.info("[MV-SYNC] Iniciando sync via {} — período: {} a {}", estrategia, dataInicio, dataFim);

        try {
            var cirurgias = resolverStrategy().buscarCirurgias(dataInicio, dataFim);

            int inseridas  = 0;
            int atualizadas = 0;

            for (MvCirurgiaRaw raw : cirurgias) {
                Optional<CirurgiaAgendada> existente = repository.findByTenantIdAndIdMv(tenantId, raw.idMv());
                CirurgiaAgendada entidade = existente.orElse(new CirurgiaAgendada());
                boolean isNova = existente.isEmpty();

                mapear(raw, entidade, tenantId);
                repository.save(entidade);

                if (isNova) inseridas++; else atualizadas++;
            }

            log.info("[MV-SYNC] Concluído — {} novas, {} atualizadas", inseridas, atualizadas);
            return SincronizacaoResultadoDto.sucesso(inicio, estrategia, cirurgias.size(), inseridas, atualizadas);

        } catch (MvIntegracaoException e) {
            log.error("[MV-SYNC] Falha: {}", e.getMessage(), e);
            return SincronizacaoResultadoDto.falha(inicio, estrategia, e.getMessage());
        }
    }

    // ─── Consultas públicas ───────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<CirurgiaAgendadaDto> listar(Long tenantId, LocalDate data,
                                            StatusCirurgiaMv status, Pageable pageable) {
        if (data != null && status != null) {
            return repository.findByTenantIdAndDataHoraInicioBetweenAndStatusMv(
                    tenantId,
                    data.atStartOfDay(),
                    data.atTime(23, 59, 59),
                    status, pageable).map(this::toDto);
        }
        if (data != null) {
            return repository.findByTenantIdAndDataHoraInicioBetween(
                    tenantId,
                    data.atStartOfDay(),
                    data.atTime(23, 59, 59),
                    pageable).map(this::toDto);
        }
        if (status != null) {
            return repository.findByTenantIdAndStatusMv(tenantId, status, pageable).map(this::toDto);
        }
        return repository.findByTenantId(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public CirurgiaAgendadaDto findById(Long tenantId, Long id) {
        CirurgiaAgendada c = repository.findById(id)
                .filter(e -> e.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("Cirurgia não encontrada: id=" + id));
        return toDto(c);
    }

    // ─── Internos ─────────────────────────────────────────────────────────────

    private MvIntegracaoStrategy resolverStrategy() {
        String tipo = configuracaoService.getValor(ModuloConfiguracao.MV, "MV_INTEGRACAO_TIPO");
        return "BANCO_DADOS".equalsIgnoreCase(tipo) ? mvBancoDadosStrategy : mvApiStrategy;
    }

    private String resolverNomeEstrategia() {
        String tipo = configuracaoService.getValor(ModuloConfiguracao.MV, "MV_INTEGRACAO_TIPO");
        return "BANCO_DADOS".equalsIgnoreCase(tipo) ? "BANCO_DADOS" : "API";
    }

    private Long obterTenantId() {
        String val = configuracaoService.getValor(ModuloConfiguracao.MV, "MV_TENANT_ID");
        if (val == null || val.isBlank()) return null;
        try { return Long.parseLong(val.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private void mapear(MvCirurgiaRaw raw, CirurgiaAgendada entidade, Long tenantId) {
        entidade.setTenantId(tenantId);
        entidade.setIdMv(raw.idMv());
        entidade.setCodigoPaciente(raw.codigoPaciente());
        entidade.setNomePaciente(raw.nomePaciente());
        entidade.setDataHoraInicio(raw.dataHoraInicio());
        entidade.setDataHoraFimPrevista(raw.dataHoraFimPrevista());
        entidade.setTipoCirurgia(raw.tipoCirurgia());
        entidade.setSalaCirurgica(raw.salaCirurgica());
        entidade.setNomeCirurgiao(raw.nomeCirurgiao());
        entidade.setStatusMv(normalizarStatus(raw.statusMv()));
        entidade.setUltimaSincronizacao(LocalDateTime.now());
    }

    private StatusCirurgiaMv normalizarStatus(String statusMv) {
        if (statusMv == null) return StatusCirurgiaMv.DESCONHECIDO;
        return switch (statusMv.toUpperCase().trim()) {
            case "AGENDADA", "SCHEDULED"            -> StatusCirurgiaMv.AGENDADA;
            case "EM_ANDAMENTO", "IN_PROGRESS"      -> StatusCirurgiaMv.EM_ANDAMENTO;
            case "REALIZADA", "COMPLETED", "DONE"   -> StatusCirurgiaMv.REALIZADA;
            case "CANCELADA", "CANCELED"            -> StatusCirurgiaMv.CANCELADA;
            case "SUSPENSA", "SUSPENDED"            -> StatusCirurgiaMv.SUSPENSA;
            default                                  -> StatusCirurgiaMv.DESCONHECIDO;
        };
    }

    private CirurgiaAgendadaDto toDto(CirurgiaAgendada c) {
        return new CirurgiaAgendadaDto(
                c.getId(), c.getTenantId(), c.getIdMv(),
                c.getCodigoPaciente(), c.getNomePaciente(),
                c.getDataHoraInicio(), c.getDataHoraFimPrevista(),
                c.getTipoCirurgia(), c.getSalaCirurgica(), c.getNomeCirurgiao(),
                c.getStatusMv(), c.getUltimaSincronizacao()
        );
    }
}
