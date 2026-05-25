package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.domain.*;
import com.erp.qualitascareapi.cme.enums.CmeEtapaExecucaoStatus;
import com.erp.qualitascareapi.cme.enums.CmeEtapaTipo;
import com.erp.qualitascareapi.cme.enums.ProcessoStatus;
import com.erp.qualitascareapi.cme.enums.TipoFluxoCME;
import com.erp.qualitascareapi.cme.repo.*;
import com.erp.qualitascareapi.cme.domain.ConferenciaKit;
import com.erp.qualitascareapi.cme.domain.SecagemMaterial;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.common.repo.EvidenciaArquivoRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProcessoReprocessamentoService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final ProcessoReprocessamentoRepository processoRepository;
    private final LimpezaManualRepository limpezaManualRepository;
    private final HigienizacaoUltrassonicaRepository higienizacaoUltrassonicaRepository;
    private final SecagemMaterialRepository secagemMaterialRepository;
    private final ConferenciaKitRepository conferenciaKitRepository;
    private final LoteEtiquetaRepository loteEtiquetaRepository;
    private final CicloEsterilizacaoRepository cicloEsterilizacaoRepository;
    private final RecebimentoMaterialRepository recebimentoRepository;
    private final EvidenciaArquivoRepository evidenciaArquivoRepository;
    private final CmeFluxoProcessoRepository fluxoProcessoRepository;
    private final CmeEtapaProcessoRepository etapaProcessoRepository;
    private final CmeEtapaCatalogoRepository etapaCatalogoRepository;
    private final CmeEtapaExecucaoRepository etapaExecucaoRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public ProcessoReprocessamentoService(TenantRepository tenantRepository,
                                          UserRepository userRepository,
                                          ProcessoReprocessamentoRepository processoRepository,
                                          LimpezaManualRepository limpezaManualRepository,
                                          HigienizacaoUltrassonicaRepository higienizacaoUltrassonicaRepository,
                                          SecagemMaterialRepository secagemMaterialRepository,
                                          ConferenciaKitRepository conferenciaKitRepository,
                                          LoteEtiquetaRepository loteEtiquetaRepository,
                                          CicloEsterilizacaoRepository cicloEsterilizacaoRepository,
                                          RecebimentoMaterialRepository recebimentoRepository,
                                          EvidenciaArquivoRepository evidenciaArquivoRepository,
                                          CmeFluxoProcessoRepository fluxoProcessoRepository,
                                          CmeEtapaProcessoRepository etapaProcessoRepository,
                                          CmeEtapaCatalogoRepository etapaCatalogoRepository,
                                          CmeEtapaExecucaoRepository etapaExecucaoRepository,
                                          TenantScopeGuard tenantScopeGuard) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.processoRepository = processoRepository;
        this.limpezaManualRepository = limpezaManualRepository;
        this.higienizacaoUltrassonicaRepository = higienizacaoUltrassonicaRepository;
        this.secagemMaterialRepository = secagemMaterialRepository;
        this.conferenciaKitRepository = conferenciaKitRepository;
        this.loteEtiquetaRepository = loteEtiquetaRepository;
        this.cicloEsterilizacaoRepository = cicloEsterilizacaoRepository;
        this.recebimentoRepository = recebimentoRepository;
        this.evidenciaArquivoRepository = evidenciaArquivoRepository;
        this.fluxoProcessoRepository = fluxoProcessoRepository;
        this.etapaProcessoRepository = etapaProcessoRepository;
        this.etapaCatalogoRepository = etapaCatalogoRepository;
        this.etapaExecucaoRepository = etapaExecucaoRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    // ---- ProcessoReprocessamento CRUD ----

    public ProcessoReprocessamentoDto createProcesso(ProcessoReprocessamentoRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        ProcessoReprocessamento processo = new ProcessoReprocessamento();
        TipoFluxoCME tipoFluxo = request.tipoFluxo() != null ? request.tipoFluxo() : TipoFluxoCME.CIRURGICO;
        processo.setTenant(tenant);
        processo.setNumeroProcesso(request.numeroProcesso());
        processo.setTipoFluxo(tipoFluxo);
        processo.setStatus(request.status() != null ? request.status() : ProcessoStatus.ABERTO);
        processo.setDataAbertura(request.dataAbertura());
        processo.setDataConclusao(request.dataConclusao());
        processo.setFluxoProcesso(resolveFluxoProcesso(request.fluxoProcessoId(), tenant.getId(), tipoFluxo));
        if (request.recebimentoId() != null) {
            RecebimentoMaterial recebimento = recebimentoRepository.findById(request.recebimentoId())
                    .orElseThrow(() -> new EntityNotFoundException("Recebimento não encontrado"));
            processo.setRecebimento(recebimento);
        }
        processo.setObservacoes(request.observacoes());
        ProcessoReprocessamento saved = processoRepository.save(processo);
        initializeEtapas(saved);
        return toProcessoDto(saved);
    }

    public Page<ProcessoReprocessamentoDto> listProcessos(Pageable pageable) {
        return processoRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable).map(this::toProcessoDto);
    }

    public ProcessoReprocessamentoDto findProcessoById(Long id) {
        return processoRepository.findById(id)
                .map(this::toProcessoDto)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado"));
    }

    public ProcessoReprocessamentoDto updateProcessoStatus(Long id, ProcessoStatus status) {
        ProcessoReprocessamento processo = processoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado"));
        processo.setStatus(status);
        syncEtapaByStatus(processo, status);
        return toProcessoDto(processoRepository.save(processo));
    }

    // ---- Fluxos e rastreabilidade ----

    public List<CmeEtapaCatalogoDto> listEtapasCatalogo() {
        return etapaCatalogoRepository.findAllByTenantIdOrderByNomeAsc(tenantScopeGuard.currentTenantId())
                .stream().map(this::toEtapaCatalogoDto).toList();
    }

    public CmeEtapaCatalogoDto createEtapaCatalogo(CmeEtapaCatalogoRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        String codigo = normalizeCodigo(request.codigo());
        if (etapaCatalogoRepository.existsByTenantIdAndCodigoIgnoreCase(tenant.getId(), codigo)) {
            throw new ApplicationException(HttpStatus.CONFLICT, "cme.etapa-catalogo.codigo-duplicado",
                    "Já existe uma etapa cadastrada com este código.");
        }
        CmeEtapaCatalogo etapa = new CmeEtapaCatalogo();
        etapa.setTenant(tenant);
        applyEtapaCatalogoRequest(etapa, request, codigo);
        return toEtapaCatalogoDto(etapaCatalogoRepository.save(etapa));
    }

    public CmeEtapaCatalogoDto updateEtapaCatalogo(Long id, CmeEtapaCatalogoRequest request) {
        CmeEtapaCatalogo etapa = etapaCatalogoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Etapa não encontrada"));
        tenantScopeGuard.checkRequestedTenant(etapa.getTenant().getId());
        String codigo = normalizeCodigo(request.codigo());
        applyEtapaCatalogoRequest(etapa, request, codigo);
        return toEtapaCatalogoDto(etapaCatalogoRepository.save(etapa));
    }

    public void deleteEtapaCatalogo(Long id) {
        CmeEtapaCatalogo etapa = etapaCatalogoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Etapa não encontrada"));
        tenantScopeGuard.checkRequestedTenant(etapa.getTenant().getId());
        etapaCatalogoRepository.delete(etapa);
    }

    public List<CmeFluxoProcessoDto> listFluxosProcesso() {
        return fluxoProcessoRepository.findAllByTenantIdOrderByTipoFluxoAscNumeroVersaoDesc(tenantScopeGuard.currentTenantId())
                .stream().map(this::toFluxoDto).toList();
    }

    public CmeFluxoProcessoDto createFluxoProcesso(CmeFluxoProcessoRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        CmeFluxoProcesso fluxo = new CmeFluxoProcesso();
        fluxo.setTenant(tenant);
        fluxo.setNome(request.nome());
        fluxo.setTipoFluxo(request.tipoFluxo());
        fluxo.setNumeroVersao(request.numeroVersao() != null ? request.numeroVersao() : 1);
        fluxo.setAtivo(request.ativo() == null || request.ativo());
        fluxo.setDataVigenciaInicio(request.dataVigenciaInicio());
        fluxo.setDataVigenciaFim(request.dataVigenciaFim());
        fluxo.setObservacoes(request.observacoes());
        CmeFluxoProcesso saved = fluxoProcessoRepository.save(fluxo);
        if (request.etapas() != null) {
            for (CmeEtapaProcessoRequest etapaRequest : request.etapas()) {
                etapaProcessoRepository.save(buildEtapa(saved, etapaRequest));
            }
        }
        return toFluxoDto(saved);
    }

    public List<CmeEtapaProcessoDto> listEtapasFluxo(Long fluxoId) {
        CmeFluxoProcesso fluxo = fluxoProcessoRepository.findById(fluxoId)
                .orElseThrow(() -> new EntityNotFoundException("Fluxo não encontrado"));
        tenantScopeGuard.checkRequestedTenant(fluxo.getTenant().getId());
        return etapaProcessoRepository.findAllByFluxoProcessoIdOrderByOrdemAsc(fluxoId)
                .stream().map(this::toEtapaDto).toList();
    }

    public CmeEtapaProcessoDto createEtapaFluxo(Long fluxoId, CmeEtapaProcessoRequest request) {
        CmeFluxoProcesso fluxo = fluxoProcessoRepository.findById(fluxoId)
                .orElseThrow(() -> new EntityNotFoundException("Fluxo não encontrado"));
        tenantScopeGuard.checkRequestedTenant(fluxo.getTenant().getId());
        return toEtapaDto(etapaProcessoRepository.save(buildEtapa(fluxo, request)));
    }

    public CmeEtapaProcessoDto updateEtapaFluxo(Long fluxoId, Long etapaId, CmeEtapaProcessoRequest request) {
        CmeEtapaProcesso etapa = findEtapaDoFluxo(fluxoId, etapaId);
        etapa.setCodigo(request.codigo());
        etapa.setNome(request.nome());
        etapa.setTipoEtapa(request.tipoEtapa());
        etapa.setOrdem(request.ordem());
        etapa.setObrigatoria(request.obrigatoria() == null || request.obrigatoria());
        etapa.setPermitePular(request.permitePular() != null && request.permitePular());
        etapa.setExigeEvidencia(request.exigeEvidencia() != null && request.exigeEvidencia());
        etapa.setExigeAprovacao(request.exigeAprovacao() != null && request.exigeAprovacao());
        etapa.setRotaDestino(request.rotaDestino());
        etapa.setObservacoes(request.observacoes());
        return toEtapaDto(etapaProcessoRepository.save(etapa));
    }

    public void deleteEtapaFluxo(Long fluxoId, Long etapaId) {
        CmeEtapaProcesso etapa = findEtapaDoFluxo(fluxoId, etapaId);
        if (etapaExecucaoRepository.existsByEtapaId(etapaId)) {
            throw new ApplicationException(HttpStatus.CONFLICT, "cme.fluxo-etapa.in-use",
                    "Não é possível excluir uma etapa já utilizada em processos da CME. Crie uma nova versão do fluxo.");
        }
        etapaProcessoRepository.delete(etapa);
    }

    public List<CmeRastreabilidadeColunaDto> getRastreabilidade(TipoFluxoCME tipoFluxo) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        List<CmeEtapaExecucao> execucoes = etapaExecucaoRepository.findAllByProcesso_Tenant_IdOrderByProcesso_DataAberturaDesc(tenantId);
        Map<Long, CmeRastreabilidadeColunaDtoBuilder> columns = new LinkedHashMap<>();
        for (CmeEtapaExecucao execucao : execucoes) {
            if (tipoFluxo != null && execucao.getProcesso().getTipoFluxo() != tipoFluxo) {
                continue;
            }
            CmeEtapaProcesso etapa = execucao.getEtapa();
            columns.computeIfAbsent(etapa.getId(), id -> new CmeRastreabilidadeColunaDtoBuilder(etapa));
            if (execucao.getStatus() == CmeEtapaExecucaoStatus.EM_ANDAMENTO) {
                columns.get(etapa.getId()).cards.add(toRastreabilidadeCard(execucao));
            }
        }

        List<CmeFluxoProcesso> fluxos = fluxoProcessoRepository.findAllByTenantIdOrderByTipoFluxoAscNumeroVersaoDesc(tenantId);
        fluxos.stream()
                .filter(CmeFluxoProcesso::isAtivo)
                .filter(fluxo -> tipoFluxo == null || fluxo.getTipoFluxo() == tipoFluxo)
                .forEach(fluxo -> etapaProcessoRepository.findAllByFluxoProcessoIdOrderByOrdemAsc(fluxo.getId())
                        .forEach(etapa -> columns.computeIfAbsent(etapa.getId(), id -> new CmeRastreabilidadeColunaDtoBuilder(etapa))));

        return columns.values().stream()
                .sorted(Comparator.comparing(builder -> builder.etapa.getOrdem()))
                .map(CmeRastreabilidadeColunaDtoBuilder::toDto)
                .toList();
    }

    // ---- LimpezaManual CRUD ----

    public LimpezaManualDto registrarLimpezaManual(LimpezaManualRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        User responsavel = userRepository.findById(request.responsavelId())
                .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
        LimpezaManual limpeza = new LimpezaManual();
        limpeza.setTenant(tenant);
        limpeza.setResponsavel(responsavel);
        if (request.processoId() != null) {
            ProcessoReprocessamento processo = processoRepository.findById(request.processoId())
                    .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado"));
            limpeza.setProcesso(processo);
        }
        limpeza.setDataHoraInicio(request.dataHoraInicio());
        limpeza.setDataHoraFim(request.dataHoraFim());
        limpeza.setProdutoUtilizado(request.produtoUtilizado());
        limpeza.setConcentracao(request.concentracao());
        limpeza.setMetodo(request.metodo());
        limpeza.setConformidade(request.conformidade());
        limpeza.setObservacoes(request.observacoes());
        limpeza.setEvidencias(loadEvidencias(request.evidenciasIds()));
        return toLimpezaDto(limpezaManualRepository.save(limpeza));
    }

    public Page<LimpezaManualDto> listLimpezas(Pageable pageable) {
        return limpezaManualRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable).map(this::toLimpezaDto);
    }

    public LimpezaManualDto findLimpezaById(Long id) {
        return limpezaManualRepository.findById(id)
                .map(this::toLimpezaDto)
                .orElseThrow(() -> new EntityNotFoundException("Limpeza manual não encontrada"));
    }

    // ---- SecagemMaterial CRUD ----

    public SecagemMaterialDto registrarSecagem(SecagemMaterialRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        User responsavel = userRepository.findById(request.responsavelId())
                .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
        SecagemMaterial secagem = new SecagemMaterial();
        secagem.setTenant(tenant);
        secagem.setResponsavel(responsavel);
        if (request.processoId() != null) {
            ProcessoReprocessamento processo = processoRepository.findById(request.processoId())
                    .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado"));
            secagem.setProcesso(processo);
        }
        secagem.setTipoSecagem(request.tipoSecagem());
        secagem.setDataHoraInicio(request.dataHoraInicio());
        secagem.setDataHoraFim(request.dataHoraFim());
        secagem.setEquipamentoDescricao(request.equipamentoDescricao());
        secagem.setConformidade(request.conformidade());
        secagem.setObservacoes(request.observacoes());
        secagem.setEvidencias(loadEvidencias(request.evidenciasIds()));
        return toSecagemDto(secagemMaterialRepository.save(secagem));
    }

    public Page<SecagemMaterialDto> listSecagens(Pageable pageable) {
        return secagemMaterialRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(this::toSecagemDto);
    }

    // ---- ConferenciaKit CRUD ----

    public ConferenciaKitDto registrarConferencia(ConferenciaKitRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        User responsavel = userRepository.findById(request.responsavelId())
                .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
        ConferenciaKit conferencia = new ConferenciaKit();
        conferencia.setTenant(tenant);
        conferencia.setResponsavel(responsavel);
        if (request.processoId() != null) {
            ProcessoReprocessamento processo = processoRepository.findById(request.processoId())
                    .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado"));
            conferencia.setProcesso(processo);
        }
        conferencia.setDataHoraConferencia(request.dataHoraConferencia());
        conferencia.setConformidade(request.conformidade());
        conferencia.setItensNaoConformes(request.itensNaoConformes());
        conferencia.setObservacoes(request.observacoes());
        conferencia.setEvidencias(loadEvidencias(request.evidenciasIds()));
        return toConferenciaDto(conferenciaKitRepository.save(conferencia));
    }

    public Page<ConferenciaKitDto> listConferencias(Pageable pageable) {
        return conferenciaKitRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(this::toConferenciaDto);
    }

    // ---- Timeline ----

    public ProcessoTimelineDto getTimeline(Long processoId) {
        ProcessoReprocessamento processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado"));

        RecebimentoMaterialDto recebimentoDto = null;
        if (processo.getRecebimento() != null) {
            RecebimentoMaterial r = processo.getRecebimento();
            recebimentoDto = new RecebimentoMaterialDto(r.getId(), r.getTenant().getId(), r.getDataHora(),
                    r.getSetorOrigem() != null ? r.getSetorOrigem().getId() : null,
                    r.getResponsavel() != null ? r.getResponsavel().getId() : null,
                    r.getQuantidadeItens(), r.getCondicaoDescricao(), r.getStatus(),
                    r.getObservacoes(), toIdSet(r.getEvidencias()));
        }

        List<LimpezaManualDto> limpezasDto = limpezaManualRepository.findByProcessoId(processoId)
                .stream().map(this::toLimpezaDto).toList();

        List<HigienizacaoUltrassonicaDto> ultrassonicasDto = higienizacaoUltrassonicaRepository.findByProcessoId(processoId)
                .stream().map(this::toUltrassonicaDto).toList();

        List<LoteEtiquetaDto> lotesDto = loteEtiquetaRepository.findByProcessoId(processoId)
                .stream().map(this::toLoteDto).toList();

        List<CicloEsterilizacaoDto> ciclosDto = cicloEsterilizacaoRepository.findByProcessoId(processoId)
                .stream().map(this::toCicloDto).toList();

        return new ProcessoTimelineDto(toProcessoDto(processo), recebimentoDto, limpezasDto,
                ultrassonicasDto, lotesDto, ciclosDto);
    }

    // ---- Private mappers ----

    private ProcessoReprocessamentoDto toProcessoDto(ProcessoReprocessamento p) {
        return new ProcessoReprocessamentoDto(p.getId(), p.getTenant().getId(), p.getNumeroProcesso(),
                p.getTipoFluxo(),
                p.getFluxoProcesso() != null ? p.getFluxoProcesso().getId() : null,
                p.getFluxoProcesso() != null ? p.getFluxoProcesso().getNome() : null,
                p.getStatus(), p.getDataAbertura(), p.getDataConclusao(),
                p.getRecebimento() != null ? p.getRecebimento().getId() : null,
                p.getObservacoes());
    }

    private CmeFluxoProcesso resolveFluxoProcesso(Long fluxoProcessoId, Long tenantId, TipoFluxoCME tipoFluxo) {
        if (fluxoProcessoId != null) {
            CmeFluxoProcesso fluxo = fluxoProcessoRepository.findById(fluxoProcessoId)
                    .orElseThrow(() -> new EntityNotFoundException("Fluxo de processo não encontrado"));
            tenantScopeGuard.checkRequestedTenant(fluxo.getTenant().getId());
            return fluxo;
        }
        return fluxoProcessoRepository.findFirstByTenantIdAndTipoFluxoAndAtivoTrueOrderByNumeroVersaoDesc(tenantId, tipoFluxo)
                .orElse(null);
    }

    private void initializeEtapas(ProcessoReprocessamento processo) {
        if (processo.getFluxoProcesso() == null || etapaExecucaoRepository.existsByProcessoId(processo.getId())) {
            return;
        }
        List<CmeEtapaProcesso> etapas = etapaProcessoRepository
                .findAllByFluxoProcessoIdOrderByOrdemAsc(processo.getFluxoProcesso().getId());
        for (int i = 0; i < etapas.size(); i++) {
            CmeEtapaExecucao execucao = new CmeEtapaExecucao();
            execucao.setProcesso(processo);
            execucao.setEtapa(etapas.get(i));
            execucao.setStatus(i == 0 ? CmeEtapaExecucaoStatus.EM_ANDAMENTO : CmeEtapaExecucaoStatus.PENDENTE);
            if (i == 0) {
                execucao.setDataHoraInicio(LocalDateTime.now());
            }
            etapaExecucaoRepository.save(execucao);
        }
    }

    private void syncEtapaByStatus(ProcessoReprocessamento processo, ProcessoStatus status) {
        CmeEtapaTipo tipo = etapaTipoFromStatus(status);
        if (tipo == null) return;
        List<CmeEtapaExecucao> execucoes = etapaExecucaoRepository.findAllByProcessoIdOrderByEtapa_OrdemAsc(processo.getId());
        if (execucoes.isEmpty()) {
            initializeEtapas(processo);
            execucoes = etapaExecucaoRepository.findAllByProcessoIdOrderByEtapa_OrdemAsc(processo.getId());
        }
        LocalDateTime now = LocalDateTime.now();
        for (CmeEtapaExecucao execucao : execucoes) {
            if (execucao.getEtapa().getTipoEtapa() == tipo) {
                execucao.setStatus(CmeEtapaExecucaoStatus.EM_ANDAMENTO);
                if (execucao.getDataHoraInicio() == null) execucao.setDataHoraInicio(now);
            } else if (execucao.getEtapa().getOrdem() < ordemAtual(execucoes, tipo)
                    && execucao.getStatus() != CmeEtapaExecucaoStatus.PULADA) {
                execucao.setStatus(CmeEtapaExecucaoStatus.CONCLUIDA);
                if (execucao.getDataHoraFim() == null) execucao.setDataHoraFim(now);
            }
            etapaExecucaoRepository.save(execucao);
        }
    }

    private Integer ordemAtual(List<CmeEtapaExecucao> execucoes, CmeEtapaTipo tipo) {
        return execucoes.stream()
                .filter(execucao -> execucao.getEtapa().getTipoEtapa() == tipo)
                .map(execucao -> execucao.getEtapa().getOrdem())
                .findFirst()
                .orElse(Integer.MAX_VALUE);
    }

    private CmeEtapaTipo etapaTipoFromStatus(ProcessoStatus status) {
        return switch (status) {
            case ABERTO -> CmeEtapaTipo.RECEBIMENTO;
            case EM_LIMPEZA_MANUAL -> CmeEtapaTipo.LIMPEZA_MANUAL;
            case EM_ULTRASSONICA -> CmeEtapaTipo.ULTRASSONICA;
            case BANHO_QUIMICO -> CmeEtapaTipo.BANHO_QUIMICO;
            case EM_SECAGEM -> CmeEtapaTipo.SECAGEM;
            case EM_CONFERENCIA -> CmeEtapaTipo.CONFERENCIA;
            case EM_MONTAGEM -> CmeEtapaTipo.MONTAGEM;
            case EM_ESTERILIZACAO -> CmeEtapaTipo.ESTERILIZACAO;
            case LIBERADO -> CmeEtapaTipo.ESTOQUE;
            case REPROVADO, CANCELADO -> null;
        };
    }

    private CmeEtapaProcesso findEtapaDoFluxo(Long fluxoId, Long etapaId) {
        CmeEtapaProcesso etapa = etapaProcessoRepository.findById(etapaId)
                .orElseThrow(() -> new EntityNotFoundException("Etapa não encontrada"));
        if (!etapa.getFluxoProcesso().getId().equals(fluxoId)) {
            throw new EntityNotFoundException("Etapa não encontrada para o fluxo informado");
        }
        tenantScopeGuard.checkRequestedTenant(etapa.getFluxoProcesso().getTenant().getId());
        return etapa;
    }

    private CmeEtapaProcesso buildEtapa(CmeFluxoProcesso fluxo, CmeEtapaProcessoRequest request) {
        CmeEtapaProcesso etapa = new CmeEtapaProcesso();
        etapa.setFluxoProcesso(fluxo);
        etapa.setCodigo(normalizeCodigo(request.codigo()));
        etapa.setNome(request.nome());
        etapa.setTipoEtapa(request.tipoEtapa());
        etapa.setOrdem(request.ordem());
        etapa.setObrigatoria(request.obrigatoria() == null || request.obrigatoria());
        etapa.setPermitePular(request.permitePular() != null && request.permitePular());
        etapa.setExigeEvidencia(request.exigeEvidencia() != null && request.exigeEvidencia());
        etapa.setExigeAprovacao(request.exigeAprovacao() != null && request.exigeAprovacao());
        etapa.setRotaDestino(request.rotaDestino());
        etapa.setObservacoes(request.observacoes());
        return etapa;
    }

    private void applyEtapaCatalogoRequest(CmeEtapaCatalogo etapa, CmeEtapaCatalogoRequest request, String codigo) {
        etapa.setCodigo(codigo);
        etapa.setNome(request.nome());
        etapa.setTipoEtapa(request.tipoEtapa());
        etapa.setObrigatoria(request.obrigatoria() == null || request.obrigatoria());
        etapa.setPermitePular(request.permitePular() != null && request.permitePular());
        etapa.setExigeEvidencia(request.exigeEvidencia() != null && request.exigeEvidencia());
        etapa.setExigeAprovacao(request.exigeAprovacao() != null && request.exigeAprovacao());
        etapa.setRotaDestino(request.rotaDestino());
        etapa.setAtivo(request.ativo() == null || request.ativo());
        etapa.setObservacoes(request.observacoes());
    }

    private String normalizeCodigo(String codigo) {
        return codigo == null ? null : codigo.trim().toUpperCase().replace(' ', '_');
    }

    private CmeEtapaCatalogoDto toEtapaCatalogoDto(CmeEtapaCatalogo etapa) {
        return new CmeEtapaCatalogoDto(etapa.getId(), etapa.getTenant().getId(), etapa.getCodigo(), etapa.getNome(),
                etapa.getTipoEtapa(), etapa.isObrigatoria(), etapa.isPermitePular(), etapa.isExigeEvidencia(),
                etapa.isExigeAprovacao(), etapa.getRotaDestino(), etapa.isAtivo(), etapa.getObservacoes());
    }

    private CmeFluxoProcessoDto toFluxoDto(CmeFluxoProcesso fluxo) {
        List<CmeEtapaProcessoDto> etapas = etapaProcessoRepository.findAllByFluxoProcessoIdOrderByOrdemAsc(fluxo.getId())
                .stream().map(this::toEtapaDto).toList();
        return new CmeFluxoProcessoDto(fluxo.getId(), fluxo.getTenant().getId(), fluxo.getNome(), fluxo.getTipoFluxo(),
                fluxo.getNumeroVersao(), fluxo.isAtivo(), fluxo.getDataVigenciaInicio(), fluxo.getDataVigenciaFim(),
                fluxo.getObservacoes(), fluxo.getCriadoEm(), etapas);
    }

    private CmeEtapaProcessoDto toEtapaDto(CmeEtapaProcesso etapa) {
        return new CmeEtapaProcessoDto(etapa.getId(), etapa.getFluxoProcesso().getId(), etapa.getCodigo(), etapa.getNome(),
                etapa.getTipoEtapa(), etapa.getOrdem(), etapa.isObrigatoria(), etapa.isPermitePular(),
                etapa.isExigeEvidencia(), etapa.isExigeAprovacao(), etapa.getRotaDestino(), etapa.getObservacoes());
    }

    private CmeRastreabilidadeCardDto toRastreabilidadeCard(CmeEtapaExecucao execucao) {
        ProcessoReprocessamento processo = execucao.getProcesso();
        CmeFluxoProcesso fluxo = processo.getFluxoProcesso();
        return new CmeRastreabilidadeCardDto(processo.getId(), processo.getNumeroProcesso(), processo.getTipoFluxo(),
                processo.getStatus(), fluxo != null ? fluxo.getId() : null, fluxo != null ? fluxo.getNome() : null,
                execucao.getId(), execucao.getEtapa().getId(), execucao.getEtapa().getNome(),
                execucao.getEtapa().getOrdem(), processo.getDataAbertura(), processo.getDataConclusao(),
                processo.getObservacoes());
    }

    private static class CmeRastreabilidadeColunaDtoBuilder {
        private final CmeEtapaProcesso etapa;
        private final List<CmeRastreabilidadeCardDto> cards = new java.util.ArrayList<>();

        private CmeRastreabilidadeColunaDtoBuilder(CmeEtapaProcesso etapa) {
            this.etapa = etapa;
        }

        private CmeRastreabilidadeColunaDto toDto() {
            return new CmeRastreabilidadeColunaDto(etapa.getId(), etapa.getCodigo(), etapa.getNome(), etapa.getTipoEtapa(),
                    etapa.getOrdem(), etapa.isObrigatoria(), etapa.isPermitePular(), cards);
        }
    }

    private LimpezaManualDto toLimpezaDto(LimpezaManual l) {
        return new LimpezaManualDto(l.getId(), l.getTenant().getId(),
                l.getProcesso() != null ? l.getProcesso().getId() : null,
                l.getResponsavel().getId(),
                l.getDataHoraInicio(), l.getDataHoraFim(),
                l.getProdutoUtilizado(), l.getConcentracao(),
                l.getMetodo(), l.getConformidade(), l.getObservacoes(),
                toIdSet(l.getEvidencias()));
    }

    private HigienizacaoUltrassonicaDto toUltrassonicaDto(HigienizacaoUltrassonica h) {
        return new HigienizacaoUltrassonicaDto(h.getId(), h.getTenant().getId(),
                h.getProcesso() != null ? h.getProcesso().getId() : null,
                h.getDataRealizacao(), h.getDataHoraInicio(), h.getDataHoraFim(),
                h.getEquipamentoDescricao(),
                h.getResponsavel() != null ? h.getResponsavel().getId() : null,
                h.getObservacoes(), toIdSet(h.getEvidencias()));
    }

    private LoteEtiquetaDto toLoteDto(LoteEtiqueta l) {
        return new LoteEtiquetaDto(l.getId(), l.getTenant().getId(),
                l.getProcesso() != null ? l.getProcesso().getId() : null,
                l.getCodigo(),
                l.getKitVersao() != null ? l.getKitVersao().getId() : null,
                l.getKitFisico() != null ? l.getKitFisico().getId() : null,
                l.getKitFisico() != null ? l.getKitFisico().getIdentificadorUnico() : null,
                l.getDataEmpacotamento(), l.getValidade(), l.getStatus(), l.getQrCode(),
                l.getMontadoPor() != null ? l.getMontadoPor().getId() : null,
                l.getDataHoraInicioMontagem(), l.getDataHoraFimMontagem(),
                l.getObservacoes(), l.getCriadoEm());
    }

    private CicloEsterilizacaoDto toCicloDto(CicloEsterilizacao c) {
        List<Long> loteIds = c.getLotes().stream().map(l -> l.getId()).toList();
        return new CicloEsterilizacaoDto(c.getId(), c.getTenant().getId(),
                c.getProcesso() != null ? c.getProcesso().getId() : null,
                c.getAutoclave().getId(),
                loteIds,
                c.getInicio(), c.getFim(), c.getDuracaoMinutos(), c.getTemperaturaMaxima(),
                c.getPressaoMaxima(), c.getStatus(),
                c.getLiberadoPor() != null ? c.getLiberadoPor().getId() : null,
                c.getObservacoes());
    }

    private SecagemMaterialDto toSecagemDto(SecagemMaterial s) {
        return new SecagemMaterialDto(s.getId(), s.getTenant().getId(),
                s.getProcesso() != null ? s.getProcesso().getId() : null,
                s.getTipoSecagem(), s.getResponsavel().getId(),
                s.getDataHoraInicio(), s.getDataHoraFim(),
                s.getEquipamentoDescricao(), s.getConformidade(),
                s.getObservacoes(), toIdSet(s.getEvidencias()));
    }

    private ConferenciaKitDto toConferenciaDto(ConferenciaKit c) {
        return new ConferenciaKitDto(c.getId(), c.getTenant().getId(),
                c.getProcesso() != null ? c.getProcesso().getId() : null,
                c.getResponsavel().getId(),
                c.getDataHoraConferencia(), c.getConformidade(),
                c.getItensNaoConformes(), c.getObservacoes(),
                toIdSet(c.getEvidencias()));
    }

    private Set<EvidenciaArquivo> loadEvidencias(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptySet();
        return new HashSet<>(evidenciaArquivoRepository.findAllById(ids));
    }

    private Set<Long> toIdSet(Set<EvidenciaArquivo> evidencias) {
        if (evidencias == null || evidencias.isEmpty()) return Collections.emptySet();
        return evidencias.stream().map(EvidenciaArquivo::getId).collect(Collectors.toSet());
    }
}
