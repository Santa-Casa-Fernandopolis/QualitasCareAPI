package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.domain.*;
import com.erp.qualitascareapi.cme.enums.CicloStatus;
import com.erp.qualitascareapi.cme.repo.*;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.common.repo.EvidenciaArquivoRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class AutoclaveService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final AutoclaveRepository autoclaveRepository;
    private final PlanoPreventivoAutoclaveRepository planoPreventivoAutoclaveRepository;
    private final ManutencaoAutoclaveRepository manutencaoAutoclaveRepository;
    private final HigienizacaoAutoclaveProfundaRepository higienizacaoAutoclaveProfundaRepository;
    private final HigienizacaoUltrassonicaRepository higienizacaoUltrassonicaRepository;
    private final TesteBowieDickRepository testeBowieDickRepository;
    private final CicloEsterilizacaoRepository cicloEsterilizacaoRepository;
    private final IndicadorQuimicoRepository indicadorQuimicoRepository;
    private final IndicadorBiologicoRepository indicadorBiologicoRepository;
    private final LoteEtiquetaRepository loteEtiquetaRepository;
    private final ProcessoReprocessamentoRepository processoRepository;
    private final EvidenciaArquivoRepository evidenciaArquivoRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public AutoclaveService(TenantRepository tenantRepository,
                            UserRepository userRepository,
                            AutoclaveRepository autoclaveRepository,
                            PlanoPreventivoAutoclaveRepository planoPreventivoAutoclaveRepository,
                            ManutencaoAutoclaveRepository manutencaoAutoclaveRepository,
                            HigienizacaoAutoclaveProfundaRepository higienizacaoAutoclaveProfundaRepository,
                            HigienizacaoUltrassonicaRepository higienizacaoUltrassonicaRepository,
                            TesteBowieDickRepository testeBowieDickRepository,
                            CicloEsterilizacaoRepository cicloEsterilizacaoRepository,
                            IndicadorQuimicoRepository indicadorQuimicoRepository,
                            IndicadorBiologicoRepository indicadorBiologicoRepository,
                            LoteEtiquetaRepository loteEtiquetaRepository,
                            ProcessoReprocessamentoRepository processoRepository,
                            EvidenciaArquivoRepository evidenciaArquivoRepository,
                            TenantScopeGuard tenantScopeGuard) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.autoclaveRepository = autoclaveRepository;
        this.planoPreventivoAutoclaveRepository = planoPreventivoAutoclaveRepository;
        this.manutencaoAutoclaveRepository = manutencaoAutoclaveRepository;
        this.higienizacaoAutoclaveProfundaRepository = higienizacaoAutoclaveProfundaRepository;
        this.higienizacaoUltrassonicaRepository = higienizacaoUltrassonicaRepository;
        this.testeBowieDickRepository = testeBowieDickRepository;
        this.cicloEsterilizacaoRepository = cicloEsterilizacaoRepository;
        this.indicadorQuimicoRepository = indicadorQuimicoRepository;
        this.indicadorBiologicoRepository = indicadorBiologicoRepository;
        this.loteEtiquetaRepository = loteEtiquetaRepository;
        this.processoRepository = processoRepository;
        this.evidenciaArquivoRepository = evidenciaArquivoRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public AutoclaveDto findAutoclaveById(Long id) {
        return autoclaveRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Autoclave não encontrada"));
    }

    public AutoclaveDto updateAutoclave(Long id, AutoclaveRequest request) {
        Autoclave autoclave = autoclaveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Autoclave não encontrada"));
        autoclave.setNome(request.nome());
        autoclave.setFabricante(request.fabricante());
        autoclave.setModelo(request.modelo());
        autoclave.setNumeroSerie(request.numeroSerie());
        autoclave.setLocalizacao(request.localizacao());
        if (request.ultimaHigienizacaoProfunda() != null) {
            autoclave.setUltimaHigienizacaoProfunda(request.ultimaHigienizacaoProfunda());
        }
        if (request.ativo() != null) {
            autoclave.setAtivo(request.ativo());
        }
        return toDto(autoclaveRepository.save(autoclave));
    }

    public AutoclaveDto updateAutoclaveStatus(Long id, Boolean ativo) {
        Autoclave autoclave = autoclaveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Autoclave não encontrada"));
        autoclave.setAtivo(ativo);
        return toDto(autoclaveRepository.save(autoclave));
    }

    public CicloEsterilizacaoDto findCicloById(Long id) {
        CicloEsterilizacao ciclo = cicloEsterilizacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ciclo não encontrado"));
        return toCicloDto(ciclo);
    }

    public CicloEsterilizacaoDto updateCicloStatus(Long id, CicloStatus status) {
        CicloEsterilizacao ciclo = cicloEsterilizacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ciclo não encontrado"));
        ciclo.setStatus(status);
        return toCicloDto(cicloEsterilizacaoRepository.save(ciclo));
    }

    public ManutencaoDto findManutencaoById(Long id) {
        ManutencaoAutoclave m = manutencaoAutoclaveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Manutenção não encontrada"));
        return new ManutencaoDto(m.getId(), m.getAutoclave().getId(), m.getTipo(), m.getStatus(),
                m.getDataAgendamento(), m.getDataExecucao(), m.getResponsavelTecnico(),
                m.getObservacoes(), toIdSet(m.getEvidencias()));
    }

    public ManutencaoDto updateManutencaoStatus(Long id, com.erp.qualitascareapi.cme.enums.ManutencaoStatus status) {
        ManutencaoAutoclave m = manutencaoAutoclaveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Manutenção não encontrada"));
        m.setStatus(status);
        ManutencaoAutoclave saved = manutencaoAutoclaveRepository.save(m);
        return new ManutencaoDto(saved.getId(), saved.getAutoclave().getId(), saved.getTipo(), saved.getStatus(),
                saved.getDataAgendamento(), saved.getDataExecucao(), saved.getResponsavelTecnico(),
                saved.getObservacoes(), toIdSet(saved.getEvidencias()));
    }

    public AutoclaveDto createAutoclave(AutoclaveRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        Autoclave autoclave = new Autoclave();
        autoclave.setTenant(tenant);
        autoclave.setNome(request.nome());
        autoclave.setFabricante(request.fabricante());
        autoclave.setModelo(request.modelo());
        autoclave.setNumeroSerie(request.numeroSerie());
        autoclave.setLocalizacao(request.localizacao());
        autoclave.setUltimaHigienizacaoProfunda(request.ultimaHigienizacaoProfunda());
        autoclave.setAtivo(request.ativo() != null ? request.ativo() : Boolean.TRUE);
        Autoclave saved = autoclaveRepository.save(autoclave);
        return toDto(saved);
    }

    public Page<AutoclaveDto> listAutoclaves(Pageable pageable) {
        return autoclaveRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable).map(this::toDto);
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

    private AutoclaveDto toDto(Autoclave autoclave) {
        return new AutoclaveDto(autoclave.getId(), autoclave.getTenant().getId(), autoclave.getNome(),
                autoclave.getFabricante(), autoclave.getModelo(), autoclave.getNumeroSerie(),
                autoclave.getLocalizacao(), autoclave.getUltimaHigienizacaoProfunda(), autoclave.getAtivo());
    }

    public PlanoPreventivoDto createPlanoPreventivo(PlanoPreventivoRequest request) {
        Autoclave autoclave = autoclaveRepository.findById(request.autoclaveId())
                .orElseThrow(() -> new EntityNotFoundException("Autoclave não encontrada"));
        PlanoPreventivoAutoclave plano = new PlanoPreventivoAutoclave();
        plano.setAutoclave(autoclave);
        plano.setPeriodicidadeDias(request.periodicidadeDias());
        plano.setLimiteCiclos(request.limiteCiclos());
        plano.setProximaExecucaoPrevista(request.proximaExecucaoPrevista());
        plano.setDescricao(request.descricao());
        PlanoPreventivoAutoclave saved = planoPreventivoAutoclaveRepository.save(plano);
        return new PlanoPreventivoDto(saved.getId(), autoclave.getId(), saved.getPeriodicidadeDias(),
                saved.getLimiteCiclos(), saved.getProximaExecucaoPrevista(), saved.getDescricao());
    }

    public Page<PlanoPreventivoDto> listPlanos(Pageable pageable) {
        return planoPreventivoAutoclaveRepository.findAllByAutoclave_TenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(p -> new PlanoPreventivoDto(p.getId(), p.getAutoclave().getId(),
                        p.getPeriodicidadeDias(), p.getLimiteCiclos(),
                        p.getProximaExecucaoPrevista(), p.getDescricao()));
    }

    public ManutencaoDto registrarManutencao(ManutencaoRequest request) {
        Autoclave autoclave = autoclaveRepository.findById(request.autoclaveId())
                .orElseThrow(() -> new EntityNotFoundException("Autoclave não encontrada"));
        ManutencaoAutoclave manutencao = new ManutencaoAutoclave();
        manutencao.setAutoclave(autoclave);
        manutencao.setTipo(request.tipo());
        if (request.status() != null) {
            manutencao.setStatus(request.status());
        }
        manutencao.setDataAgendamento(request.dataAgendamento());
        manutencao.setDataExecucao(request.dataExecucao());
        manutencao.setResponsavelTecnico(request.responsavelTecnico());
        manutencao.setObservacoes(request.observacoes());
        manutencao.setEvidencias(loadEvidencias(request.evidenciasIds()));
        ManutencaoAutoclave saved = manutencaoAutoclaveRepository.save(manutencao);
        return new ManutencaoDto(saved.getId(), autoclave.getId(), saved.getTipo(), saved.getStatus(),
                saved.getDataAgendamento(), saved.getDataExecucao(), saved.getResponsavelTecnico(),
                saved.getObservacoes(), toIdSet(saved.getEvidencias()));
    }

    public Page<ManutencaoDto> listManutencoes(Pageable pageable) {
        return manutencaoAutoclaveRepository.findAllByAutoclave_TenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(m -> new ManutencaoDto(m.getId(), m.getAutoclave().getId(), m.getTipo(), m.getStatus(),
                        m.getDataAgendamento(), m.getDataExecucao(), m.getResponsavelTecnico(),
                        m.getObservacoes(), toIdSet(m.getEvidencias())));
    }

    public HigienizacaoAutoclaveProfundaDto registrarHigienizacaoAutoclave(HigienizacaoAutoclaveProfundaRequest request) {
        Autoclave autoclave = autoclaveRepository.findById(request.autoclaveId())
                .orElseThrow(() -> new EntityNotFoundException("Autoclave não encontrada"));
        HigienizacaoAutoclaveProfunda higienizacao = new HigienizacaoAutoclaveProfunda();
        higienizacao.setAutoclave(autoclave);
        higienizacao.setDataRealizacao(request.dataRealizacao());
        if (request.responsavelId() != null) {
            User responsavel = userRepository.findById(request.responsavelId())
                    .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
            higienizacao.setResponsavel(responsavel);
        }
        higienizacao.setObservacoes(request.observacoes());
        higienizacao.setEvidencias(loadEvidencias(request.evidenciasIds()));
        HigienizacaoAutoclaveProfunda saved = higienizacaoAutoclaveProfundaRepository.save(higienizacao);
        if (request.responsavelId() != null) {
            autoclave.setUltimaHigienizacaoProfunda(request.dataRealizacao());
        }
        return new HigienizacaoAutoclaveProfundaDto(saved.getId(), autoclave.getId(), saved.getDataRealizacao(),
                saved.getResponsavel() != null ? saved.getResponsavel().getId() : null,
                saved.getObservacoes(), toIdSet(saved.getEvidencias()));
    }

    public Page<HigienizacaoAutoclaveProfundaDto> listHigienizacoesAutoclave(Pageable pageable) {
        return higienizacaoAutoclaveProfundaRepository.findAllByAutoclave_TenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(h -> new HigienizacaoAutoclaveProfundaDto(h.getId(), h.getAutoclave().getId(),
                        h.getDataRealizacao(), h.getResponsavel() != null ? h.getResponsavel().getId() : null,
                        h.getObservacoes(), toIdSet(h.getEvidencias())));
    }

    public HigienizacaoUltrassonicaDto registrarHigienizacaoUltrassonica(HigienizacaoUltrassonicaRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        HigienizacaoUltrassonica higienizacao = new HigienizacaoUltrassonica();
        higienizacao.setTenant(tenant);
        if (request.processoId() != null) {
            ProcessoReprocessamento processo = processoRepository.findById(request.processoId())
                    .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado"));
            higienizacao.setProcesso(processo);
        }
        higienizacao.setDataRealizacao(request.dataRealizacao());
        higienizacao.setDataHoraInicio(request.dataHoraInicio());
        higienizacao.setDataHoraFim(request.dataHoraFim());
        higienizacao.setEquipamentoDescricao(request.equipamentoDescricao());
        if (request.responsavelId() != null) {
            User responsavel = userRepository.findById(request.responsavelId())
                    .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
            higienizacao.setResponsavel(responsavel);
        }
        higienizacao.setObservacoes(request.observacoes());
        higienizacao.setEvidencias(loadEvidencias(request.evidenciasIds()));
        HigienizacaoUltrassonica saved = higienizacaoUltrassonicaRepository.save(higienizacao);
        return new HigienizacaoUltrassonicaDto(saved.getId(), tenant.getId(),
                saved.getProcesso() != null ? saved.getProcesso().getId() : null,
                saved.getDataRealizacao(), saved.getDataHoraInicio(), saved.getDataHoraFim(),
                saved.getEquipamentoDescricao(),
                saved.getResponsavel() != null ? saved.getResponsavel().getId() : null,
                saved.getObservacoes(), toIdSet(saved.getEvidencias()));
    }

    public Page<HigienizacaoUltrassonicaDto> listHigienizacoesUltrassonica(Pageable pageable) {
        return higienizacaoUltrassonicaRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(h -> new HigienizacaoUltrassonicaDto(h.getId(), h.getTenant().getId(),
                        h.getProcesso() != null ? h.getProcesso().getId() : null,
                        h.getDataRealizacao(), h.getDataHoraInicio(), h.getDataHoraFim(),
                        h.getEquipamentoDescricao(),
                        h.getResponsavel() != null ? h.getResponsavel().getId() : null,
                        h.getObservacoes(), toIdSet(h.getEvidencias())));
    }

    public TesteBowieDickDto registrarTesteBowieDick(TesteBowieDickRequest request) {
        Autoclave autoclave = autoclaveRepository.findById(request.autoclaveId())
                .orElseThrow(() -> new EntityNotFoundException("Autoclave não encontrada"));
        TesteBowieDick teste = new TesteBowieDick();
        teste.setAutoclave(autoclave);
        teste.setDataExecucao(request.dataExecucao());
        teste.setResultado(request.resultado());
        if (request.executadoPorId() != null) {
            User operador = userRepository.findById(request.executadoPorId())
                    .orElseThrow(() -> new EntityNotFoundException("Operador não encontrado"));
            teste.setExecutadoPor(operador);
        }
        teste.setObservacoes(request.observacoes());
        teste.setEvidencias(loadEvidencias(request.evidenciasIds()));
        TesteBowieDick saved = testeBowieDickRepository.save(teste);
        return new TesteBowieDickDto(saved.getId(), autoclave.getId(), saved.getDataExecucao(), saved.getResultado(),
                saved.getExecutadoPor() != null ? saved.getExecutadoPor().getId() : null,
                saved.getObservacoes(), toIdSet(saved.getEvidencias()));
    }

    public Page<TesteBowieDickDto> listTestesBowieDick(Pageable pageable) {
        return testeBowieDickRepository.findAllByAutoclave_TenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(t -> new TesteBowieDickDto(t.getId(), t.getAutoclave().getId(), t.getDataExecucao(), t.getResultado(),
                        t.getExecutadoPor() != null ? t.getExecutadoPor().getId() : null,
                        t.getObservacoes(), toIdSet(t.getEvidencias())));
    }

    public CicloEsterilizacaoDto registrarCiclo(CicloEsterilizacaoRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        Autoclave autoclave = autoclaveRepository.findById(request.autoclaveId())
                .orElseThrow(() -> new EntityNotFoundException("Autoclave não encontrada"));
        CicloEsterilizacao ciclo = new CicloEsterilizacao();
        ciclo.setTenant(tenant);
        ciclo.setAutoclave(autoclave);
        if (request.processoId() != null) {
            ProcessoReprocessamento processo = processoRepository.findById(request.processoId())
                    .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado"));
            ciclo.setProcesso(processo);
        }
        ciclo.setInicio(request.inicio());
        ciclo.setFim(request.fim());
        ciclo.setDuracaoMinutos(request.duracaoMinutos());
        ciclo.setTemperaturaMaxima(request.temperaturaMaxima());
        ciclo.setPressaoMaxima(request.pressaoMaxima());
        ciclo.setStatus(request.status() != null ? request.status() : CicloStatus.AGENDADO);
        if (request.liberadoPorId() != null) {
            User liberadoPor = userRepository.findById(request.liberadoPorId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário responsável não encontrado"));
            ciclo.setLiberadoPor(liberadoPor);
        }
        ciclo.setObservacoes(request.observacoes());
        CicloEsterilizacao saved = cicloEsterilizacaoRepository.save(ciclo);
        if (request.loteIds() != null && !request.loteIds().isEmpty()) {
            for (Long loteId : request.loteIds()) {
                LoteEtiqueta lote = loteEtiquetaRepository.findById(loteId)
                        .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado: " + loteId));
                lote.setCicloEsterilizacao(saved);
                loteEtiquetaRepository.save(lote);
            }
        }
        return toCicloDto(cicloEsterilizacaoRepository.findById(saved.getId()).orElseThrow());
    }

    public Page<CicloEsterilizacaoDto> listCiclos(Pageable pageable) {
        return cicloEsterilizacaoRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable).map(this::toCicloDto);
    }

    public IndicadorQuimicoDto registrarIndicadorQuimico(IndicadorQuimicoRequest request) {
        CicloEsterilizacao ciclo = cicloEsterilizacaoRepository.findById(request.cicloId())
                .orElseThrow(() -> new EntityNotFoundException("Ciclo não encontrado"));
        IndicadorQuimico indicador = new IndicadorQuimico();
        indicador.setCiclo(ciclo);
        indicador.setTipo(request.tipo());
        indicador.setResultado(request.resultado());
        indicador.setObservacoes(request.observacoes());
        indicador.setEvidencias(loadEvidencias(request.evidenciasIds()));
        IndicadorQuimico saved = indicadorQuimicoRepository.save(indicador);
        return new IndicadorQuimicoDto(saved.getId(), ciclo.getId(), saved.getTipo(), saved.getResultado(),
                saved.getObservacoes(), toIdSet(saved.getEvidencias()));
    }

    public Page<IndicadorQuimicoDto> listIndicadoresQuimicos(Pageable pageable) {
        return indicadorQuimicoRepository.findAllByCiclo_TenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(i -> new IndicadorQuimicoDto(i.getId(), i.getCiclo().getId(), i.getTipo(), i.getResultado(),
                        i.getObservacoes(), toIdSet(i.getEvidencias())));
    }

    public IndicadorBiologicoDto registrarIndicadorBiologico(IndicadorBiologicoRequest request) {
        CicloEsterilizacao ciclo = cicloEsterilizacaoRepository.findById(request.cicloId())
                .orElseThrow(() -> new EntityNotFoundException("Ciclo não encontrado"));
        IndicadorBiologico indicador = new IndicadorBiologico();
        indicador.setCiclo(ciclo);
        indicador.setLoteIndicador(request.loteIndicador());
        indicador.setIncubadora(request.incubadora());
        indicador.setLeituraEm(request.leituraEm());
        indicador.setResultado(request.resultado());
        indicador.setObservacoes(request.observacoes());
        indicador.setEvidencias(loadEvidencias(request.evidenciasIds()));
        IndicadorBiologico saved = indicadorBiologicoRepository.save(indicador);
        return new IndicadorBiologicoDto(saved.getId(), ciclo.getId(), saved.getLoteIndicador(), saved.getIncubadora(),
                saved.getLeituraEm(), saved.getResultado(), saved.getObservacoes(), toIdSet(saved.getEvidencias()));
    }

    public Page<IndicadorBiologicoDto> listIndicadoresBiologicos(Pageable pageable) {
        return indicadorBiologicoRepository.findAllByCiclo_TenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(i -> new IndicadorBiologicoDto(i.getId(), i.getCiclo().getId(), i.getLoteIndicador(),
                        i.getIncubadora(), i.getLeituraEm(), i.getResultado(), i.getObservacoes(),
                        toIdSet(i.getEvidencias())));
    }

    private Set<EvidenciaArquivo> loadEvidencias(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(evidenciaArquivoRepository.findAllById(ids));
    }

    private Set<Long> toIdSet(Set<EvidenciaArquivo> evidencias) {
        if (evidencias == null || evidencias.isEmpty()) {
            return Collections.emptySet();
        }
        return evidencias.stream().map(EvidenciaArquivo::getId).collect(java.util.stream.Collectors.toSet());
    }
}
