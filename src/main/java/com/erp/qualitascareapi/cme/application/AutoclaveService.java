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
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
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
    private final EvidenciaArquivoRepository evidenciaArquivoRepository;

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
                            EvidenciaArquivoRepository evidenciaArquivoRepository) {
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
        this.evidenciaArquivoRepository = evidenciaArquivoRepository;
    }

    public AutoclaveDto createAutoclave(AutoclaveRequest request) {
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
        return autoclaveRepository.findAll(pageable).map(this::toDto);
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
        return planoPreventivoAutoclaveRepository.findAll(pageable)
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
        return manutencaoAutoclaveRepository.findAll(pageable)
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
        return higienizacaoAutoclaveProfundaRepository.findAll(pageable)
                .map(h -> new HigienizacaoAutoclaveProfundaDto(h.getId(), h.getAutoclave().getId(),
                        h.getDataRealizacao(), h.getResponsavel() != null ? h.getResponsavel().getId() : null,
                        h.getObservacoes(), toIdSet(h.getEvidencias())));
    }

    public HigienizacaoUltrassonicaDto registrarHigienizacaoUltrassonica(HigienizacaoUltrassonicaRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        HigienizacaoUltrassonica higienizacao = new HigienizacaoUltrassonica();
        higienizacao.setTenant(tenant);
        higienizacao.setDataRealizacao(request.dataRealizacao());
        higienizacao.setEquipamentoDescricao(request.equipamentoDescricao());
        if (request.responsavelId() != null) {
            User responsavel = userRepository.findById(request.responsavelId())
                    .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
            higienizacao.setResponsavel(responsavel);
        }
        higienizacao.setObservacoes(request.observacoes());
        higienizacao.setEvidencias(loadEvidencias(request.evidenciasIds()));
        HigienizacaoUltrassonica saved = higienizacaoUltrassonicaRepository.save(higienizacao);
        return new HigienizacaoUltrassonicaDto(saved.getId(), tenant.getId(), saved.getDataRealizacao(),
                saved.getEquipamentoDescricao(),
                saved.getResponsavel() != null ? saved.getResponsavel().getId() : null,
                saved.getObservacoes(), toIdSet(saved.getEvidencias()));
    }

    public Page<HigienizacaoUltrassonicaDto> listHigienizacoesUltrassonica(Pageable pageable) {
        return higienizacaoUltrassonicaRepository.findAll(pageable)
                .map(h -> new HigienizacaoUltrassonicaDto(h.getId(), h.getTenant().getId(), h.getDataRealizacao(),
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
        return testeBowieDickRepository.findAll(pageable)
                .map(t -> new TesteBowieDickDto(t.getId(), t.getAutoclave().getId(), t.getDataExecucao(), t.getResultado(),
                        t.getExecutadoPor() != null ? t.getExecutadoPor().getId() : null,
                        t.getObservacoes(), toIdSet(t.getEvidencias())));
    }

    public CicloEsterilizacaoDto registrarCiclo(CicloEsterilizacaoRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        Autoclave autoclave = autoclaveRepository.findById(request.autoclaveId())
                .orElseThrow(() -> new EntityNotFoundException("Autoclave não encontrada"));
        CicloEsterilizacao ciclo = new CicloEsterilizacao();
        ciclo.setTenant(tenant);
        ciclo.setAutoclave(autoclave);
        if (request.loteId() != null) {
            LoteEtiqueta lote = loteEtiquetaRepository.findById(request.loteId())
                    .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));
            ciclo.setLoteEtiqueta(lote);
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
        return new CicloEsterilizacaoDto(saved.getId(), tenant.getId(), autoclave.getId(),
                saved.getLoteEtiqueta() != null ? saved.getLoteEtiqueta().getId() : null,
                saved.getInicio(), saved.getFim(), saved.getDuracaoMinutos(), saved.getTemperaturaMaxima(),
                saved.getPressaoMaxima(), saved.getStatus(),
                saved.getLiberadoPor() != null ? saved.getLiberadoPor().getId() : null,
                saved.getObservacoes());
    }

    public Page<CicloEsterilizacaoDto> listCiclos(Pageable pageable) {
        return cicloEsterilizacaoRepository.findAll(pageable)
                .map(c -> new CicloEsterilizacaoDto(c.getId(), c.getTenant().getId(), c.getAutoclave().getId(),
                        c.getLoteEtiqueta() != null ? c.getLoteEtiqueta().getId() : null,
                        c.getInicio(), c.getFim(), c.getDuracaoMinutos(), c.getTemperaturaMaxima(),
                        c.getPressaoMaxima(), c.getStatus(),
                        c.getLiberadoPor() != null ? c.getLiberadoPor().getId() : null,
                        c.getObservacoes()));
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
        return indicadorQuimicoRepository.findAll(pageable)
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
        return indicadorBiologicoRepository.findAll(pageable)
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
