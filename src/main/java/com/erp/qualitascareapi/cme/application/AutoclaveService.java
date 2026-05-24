package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.domain.*;
import com.erp.qualitascareapi.cme.enums.BowieDickStatus;
import com.erp.qualitascareapi.cme.enums.CicloStatus;
import com.erp.qualitascareapi.cme.repo.*;
import com.erp.qualitascareapi.common.api.dto.EvidenciaArquivoDto;
import com.erp.qualitascareapi.common.application.EvidenciaArquivoStorageService;
import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.common.repo.EvidenciaArquivoRepository;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.enums.OrgRoleType;
import com.erp.qualitascareapi.iam.enums.TipoSetor;
import com.erp.qualitascareapi.iam.repo.OrgRoleAssignmentRepository;
import com.erp.qualitascareapi.iam.repo.SetorRepository;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.notificacao.application.NotificacaoService;
import com.erp.qualitascareapi.notificacao.enums.NivelNotificacao;
import com.erp.qualitascareapi.notificacao.enums.TipoNotificacao;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import com.erp.qualitascareapi.security.app.AuthContext;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class AutoclaveService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final SetorRepository setorRepository;
    private final OrgRoleAssignmentRepository orgRoleAssignmentRepository;
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
    private final EvidenciaArquivoStorageService evidenciaArquivoStorageService;
    private final NotificacaoService notificacaoService;
    private final TenantScopeGuard tenantScopeGuard;

    public AutoclaveService(TenantRepository tenantRepository,
                            UserRepository userRepository,
                            SetorRepository setorRepository,
                            OrgRoleAssignmentRepository orgRoleAssignmentRepository,
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
                            EvidenciaArquivoStorageService evidenciaArquivoStorageService,
                            NotificacaoService notificacaoService,
                            TenantScopeGuard tenantScopeGuard) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.setorRepository = setorRepository;
        this.orgRoleAssignmentRepository = orgRoleAssignmentRepository;
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
        this.evidenciaArquivoStorageService = evidenciaArquivoStorageService;
        this.notificacaoService = notificacaoService;
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

    public EvidenciaArquivoDto uploadTesteBowieDickImagem(MultipartFile file) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        User autor = currentUserOrNull();
        EvidenciaArquivo evidencia = evidenciaArquivoStorageService.storeImage(tenant, file, autor, "cme/bowie-dick");
        return toEvidenciaDto(evidencia);
    }

    @Transactional(readOnly = true)
    public EvidenciaArquivo findTesteBowieDickImagem(Long evidenciaId) {
        return evidenciaArquivoRepository.findByTenant_IdAndId(tenantScopeGuard.currentTenantId(), evidenciaId)
                .orElseThrow(() -> new EntityNotFoundException("Imagem do Teste Bowie-Dick não encontrada"));
    }

    @Transactional(readOnly = true)
    public Resource loadTesteBowieDickImagem(EvidenciaArquivo evidencia) {
        return evidenciaArquivoStorageService.loadAsResource(evidencia);
    }

    public TesteBowieDickDto registrarTesteBowieDick(TesteBowieDickRequest request) {
        Autoclave autoclave = autoclaveRepository.findById(request.autoclaveId())
                .orElseThrow(() -> new EntityNotFoundException("Autoclave não encontrada"));
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (!autoclave.getTenant().getId().equals(tenantId)) {
            throw new ApplicationException(
                    HttpStatus.FORBIDDEN,
                    "bowie-dick.autoclave.invalid-tenant",
                    "A autoclave informada não pertence ao tenant atual."
            );
        }
        Setor setorCme = setorRepository.findFirstByTenantIdAndTipoOrderByNomeAsc(tenantId, TipoSetor.CME)
                .orElseThrow(() -> new ApplicationException(
                        HttpStatus.CONFLICT,
                        "bowie-dick.setor-cme.not-found",
                        "Cadastre um setor do tipo CME antes de registrar o Teste Bowie-Dick."
                ));
        User supervisor = setorCme.getSupervisor();
        if (supervisor == null) {
            throw new ApplicationException(
                    HttpStatus.CONFLICT,
                    "bowie-dick.supervisor.not-found",
                    "Defina o supervisor do setor CME antes de registrar o Teste Bowie-Dick."
            );
        }
        TesteBowieDick existente = testeBowieDickRepository
                .findByAutoclave_IdAndDataExecucao(request.autoclaveId(), request.dataExecucao())
                .orElse(null);
        if (existente != null) {
            return toTesteBowieDickDto(existente);
        }
        TesteBowieDick teste = new TesteBowieDick();
        teste.setAutoclave(autoclave);
        teste.setDataExecucao(request.dataExecucao());
        teste.setResultado(request.resultado());
        User operador = userRepository.findById(request.executadoPorId())
                .filter(user -> user.getTenant() != null && user.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("Operador não encontrado"));
        teste.setExecutadoPor(operador);
        teste.setValidador(supervisor);
        teste.setStatus(BowieDickStatus.PENDENTE_VALIDACAO);
        teste.setObservacoes(request.observacoes());
        teste.setEvidencias(loadEvidencias(request.evidenciasIds()));
        TesteBowieDick saved = testeBowieDickRepository.save(teste);
        notificacaoService.gerar(
                autoclave.getTenant().getId(),
                TipoNotificacao.CME_BOWIE_DICK_VALIDACAO_SOLICITADA,
                NivelNotificacao.ALERTA,
                "Teste Bowie-Dick aguardando validação",
                "Valide o teste Bowie-Dick da autoclave " + autoclave.getNome() + " em " + saved.getDataExecucao() + ".",
                saved.getId(),
                "BOWIE_DICK",
                supervisor.getId()
        );
        return toTesteBowieDickDto(saved);
    }

    public TesteBowieDickDto atualizarTesteBowieDick(Long id, TesteBowieDickRequest request) {
        TesteBowieDick teste = testeBowieDickRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Teste Bowie-Dick não encontrado"));
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (!teste.getAutoclave().getTenant().getId().equals(tenantId)) {
            throw new ApplicationException(
                    HttpStatus.FORBIDDEN,
                    "bowie-dick.invalid-tenant",
                    "O Teste Bowie-Dick informado não pertence ao tenant atual."
            );
        }
        if (teste.getStatus() != BowieDickStatus.PENDENTE_VALIDACAO) {
            throw new ApplicationException(
                    HttpStatus.CONFLICT,
                    "bowie-dick.already-validated",
                    "Não é possível alterar um Teste Bowie-Dick já validado."
            );
        }
        AuthContext context = tenantScopeGuard.currentContext();
        Long userId = context.userId();
        if (teste.getExecutadoPor() == null || userId == null || !teste.getExecutadoPor().getId().equals(userId)) {
            throw new ApplicationException(
                    HttpStatus.FORBIDDEN,
                    "bowie-dick.operator.invalid",
                    "Somente o operador que lançou o teste pode alterar enquanto estiver pendente de validação."
            );
        }

        Autoclave autoclave = autoclaveRepository.findById(request.autoclaveId())
                .orElseThrow(() -> new EntityNotFoundException("Autoclave não encontrada"));
        if (!autoclave.getTenant().getId().equals(tenantId)) {
            throw new ApplicationException(
                    HttpStatus.FORBIDDEN,
                    "bowie-dick.autoclave.invalid-tenant",
                    "A autoclave informada não pertence ao tenant atual."
            );
        }
        TesteBowieDick existente = testeBowieDickRepository
                .findByAutoclave_IdAndDataExecucao(request.autoclaveId(), request.dataExecucao())
                .orElse(null);
        if (existente != null && !existente.getId().equals(id)) {
            throw new ApplicationException(
                    HttpStatus.CONFLICT,
                    "bowie-dick.duplicate",
                    "Já existe um Teste Bowie-Dick para esta autoclave na data informada."
            );
        }

        teste.setAutoclave(autoclave);
        teste.setDataExecucao(request.dataExecucao());
        teste.setResultado(request.resultado());
        teste.setObservacoes(request.observacoes());
        teste.setEvidencias(loadEvidencias(request.evidenciasIds()));
        return toTesteBowieDickDto(testeBowieDickRepository.save(teste));
    }

    public void excluirTesteBowieDick(Long id) {
        TesteBowieDick teste = testeBowieDickRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Teste Bowie-Dick não encontrado"));
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (!teste.getAutoclave().getTenant().getId().equals(tenantId)) {
            throw new ApplicationException(
                    HttpStatus.FORBIDDEN,
                    "bowie-dick.invalid-tenant",
                    "O Teste Bowie-Dick informado não pertence ao tenant atual."
            );
        }
        if (teste.getStatus() != BowieDickStatus.PENDENTE_VALIDACAO) {
            throw new ApplicationException(
                    HttpStatus.CONFLICT,
                    "bowie-dick.already-validated",
                    "Não é possível excluir um Teste Bowie-Dick já validado."
            );
        }
        AuthContext context = tenantScopeGuard.currentContext();
        Long userId = context.userId();
        if (teste.getExecutadoPor() == null || userId == null || !teste.getExecutadoPor().getId().equals(userId)) {
            throw new ApplicationException(
                    HttpStatus.FORBIDDEN,
                    "bowie-dick.operator.invalid",
                    "Somente o operador que lançou o teste pode excluir enquanto estiver pendente de validação."
            );
        }
        testeBowieDickRepository.delete(teste);
    }

    public Page<TesteBowieDickDto> listTestesBowieDick(Pageable pageable) {
        return testeBowieDickRepository.findAllByAutoclave_Tenant_Id(tenantScopeGuard.currentTenantId(), pageable)
                .map(this::toTesteBowieDickDto);
    }

    public TesteBowieDickDto validarTesteBowieDick(Long id, boolean aprovado, BowieDickValidacaoRequest request) {
        TesteBowieDick teste = testeBowieDickRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Teste Bowie-Dick não encontrado"));
        AuthContext context = tenantScopeGuard.currentContext();
        Long userId = context.userId();
        if (teste.getValidador() == null || userId == null || !teste.getValidador().getId().equals(userId)) {
            throw new ApplicationException(
                    HttpStatus.FORBIDDEN,
                    "bowie-dick.validator.invalid",
                    "Somente o supervisor do setor pode validar este Teste Bowie-Dick."
            );
        }
        teste.setStatus(aprovado ? BowieDickStatus.APROVADO : BowieDickStatus.REPROVADO);
        teste.setValidadoEm(LocalDateTime.now());
        teste.setParecerValidacao(request != null ? request.parecer() : null);
        TesteBowieDick saved = testeBowieDickRepository.save(teste);
        if (!aprovado) {
            notificarQualidadeBowieDickReprovado(saved);
        }
        return toTesteBowieDickDto(saved);
    }

    private void notificarQualidadeBowieDickReprovado(TesteBowieDick teste) {
        Long tenantId = teste.getAutoclave().getTenant().getId();
        String titulo = "Teste Bowie-Dick reprovado";
        String mensagem = "O teste Bowie-Dick da autoclave " + teste.getAutoclave().getNome()
                + " em " + teste.getDataExecucao()
                + " foi reprovado por " + displayName(teste.getValidador()) + "."
                + parecerResumo(teste.getParecerValidacao());

        Set<Long> destinatarios = new HashSet<>();
        List.of(OrgRoleType.QUALIDADE_GERENTE, OrgRoleType.QUALIDADE_SUPERVISOR).forEach(roleType ->
                orgRoleAssignmentRepository
                        .findAtivosPorPapelESetorTipo(tenantId, roleType, TipoSetor.QUALIDADE)
                        .stream()
                        .map(assignment -> assignment.getUser())
                        .filter(user -> user != null && user.getStatus() != null && user.getStatus().isActive())
                        .map(User::getId)
                        .forEach(destinatarios::add)
        );
        userRepository.findAllByTenantIdAndRoleName(tenantId, "ADMIN_QUALIDADE")
                .stream()
                .filter(user -> user != null && user.getStatus() != null && user.getStatus().isActive())
                .map(User::getId)
                .forEach(destinatarios::add);

        if (destinatarios.isEmpty()) {
            notificacaoService.gerar(
                    tenantId,
                    TipoNotificacao.CME_BOWIE_DICK_REPROVADO_QUALIDADE,
                    NivelNotificacao.CRITICO,
                    titulo,
                    mensagem,
                    teste.getId(),
                    "BOWIE_DICK"
            );
            return;
        }

        destinatarios.forEach(usuarioId -> notificacaoService.gerar(
                tenantId,
                TipoNotificacao.CME_BOWIE_DICK_REPROVADO_QUALIDADE,
                NivelNotificacao.CRITICO,
                titulo,
                mensagem,
                teste.getId(),
                "BOWIE_DICK",
                usuarioId
        ));
    }

    private String parecerResumo(String parecer) {
        if (parecer == null || parecer.isBlank()) {
            return "";
        }
        String trimmed = parecer.trim();
        if (trimmed.length() > 180) {
            trimmed = trimmed.substring(0, 177) + "...";
        }
        return " Parecer: " + trimmed;
    }

    private TesteBowieDickDto toTesteBowieDickDto(TesteBowieDick t) {
        return new TesteBowieDickDto(
                t.getId(),
                t.getAutoclave().getId(),
                t.getDataExecucao(),
                t.getResultado(),
                t.getExecutadoPor() != null ? t.getExecutadoPor().getId() : null,
                displayName(t.getExecutadoPor()),
                t.getValidador() != null ? t.getValidador().getId() : null,
                displayName(t.getValidador()),
                t.getStatus() != null ? t.getStatus() : BowieDickStatus.PENDENTE_VALIDACAO,
                t.getValidadoEm(),
                t.getParecerValidacao(),
                t.getObservacoes(),
                toIdSet(t.getEvidencias())
        );
    }

    private String displayName(User user) {
        if (user == null) {
            return null;
        }
        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            return user.getFullName();
        }
        return user.getUsername();
    }

    private EvidenciaArquivoDto toEvidenciaDto(EvidenciaArquivo evidencia) {
        return new EvidenciaArquivoDto(
                evidencia.getId(),
                evidencia.getNomeArquivo(),
                evidencia.getContentType(),
                evidencia.getTamanhoBytes()
        );
    }

    private User currentUserOrNull() {
        AuthContext context = tenantScopeGuard.currentContext();
        if (context.userId() == null) {
            return null;
        }
        return userRepository.findById(context.userId()).orElse(null);
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
