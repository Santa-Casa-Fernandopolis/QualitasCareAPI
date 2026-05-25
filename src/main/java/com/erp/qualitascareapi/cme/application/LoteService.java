package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.domain.IndicadorQuimico;
import com.erp.qualitascareapi.cme.domain.IndicadorBiologico;
import com.erp.qualitascareapi.cme.domain.CmeKitFisicoLoteView;
import com.erp.qualitascareapi.cme.domain.InstrumentoFisico;
import com.erp.qualitascareapi.cme.domain.KitFisico;
import com.erp.qualitascareapi.cme.domain.KitFisicoInstrumento;
import com.erp.qualitascareapi.cme.domain.LoteEtiqueta;
import com.erp.qualitascareapi.cme.domain.MovimentacaoCME;
import com.erp.qualitascareapi.cme.domain.ProcessoReprocessamento;
import com.erp.qualitascareapi.cme.domain.TesteBowieDick;
import com.erp.qualitascareapi.cme.enums.LoteStatus;
import com.erp.qualitascareapi.cme.enums.MovimentacaoTipo;
import com.erp.qualitascareapi.cme.enums.StatusAprovacaoCme;
import com.erp.qualitascareapi.cme.enums.TipoFluxoCME;
import com.erp.qualitascareapi.cme.repo.IndicadorQuimicoRepository;
import com.erp.qualitascareapi.cme.repo.IndicadorBiologicoRepository;
import com.erp.qualitascareapi.cme.repo.CmeKitFisicoLoteViewRepository;
import com.erp.qualitascareapi.cme.repo.KitFisicoInstrumentoRepository;
import com.erp.qualitascareapi.cme.repo.KitFisicoRepository;
import com.erp.qualitascareapi.cme.repo.LoteEtiquetaRepository;
import com.erp.qualitascareapi.cme.repo.MovimentacaoCMERepository;
import com.erp.qualitascareapi.cme.repo.ProcessoReprocessamentoRepository;
import com.erp.qualitascareapi.cme.repo.TesteBowieDickRepository;
import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.core.domain.KitProcedimento;
import com.erp.qualitascareapi.core.domain.KitVersion;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.core.repo.KitVersionRepository;
import com.erp.qualitascareapi.iam.repo.SetorRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.integracao.mv.domain.CirurgiaAgendada;
import com.erp.qualitascareapi.integracao.mv.repo.CirurgiaAgendadaRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Transactional
public class LoteService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final SetorRepository setorRepository;
    private final LoteEtiquetaRepository loteEtiquetaRepository;
    private final CmeKitFisicoLoteViewRepository kitFisicoLoteViewRepository;
    private final KitVersionRepository kitVersionRepository;
    private final MovimentacaoCMERepository movimentacaoRepository;
    private final IndicadorQuimicoRepository indicadorQuimicoRepository;
    private final IndicadorBiologicoRepository indicadorBiologicoRepository;
    private final TesteBowieDickRepository testeBowieDickRepository;
    private final ProcessoReprocessamentoRepository processoRepository;
    private final ProcessoReprocessamentoService processoReprocessamentoService;
    private final KitFisicoRepository kitFisicoRepository;
    private final KitFisicoInstrumentoRepository kitFisicoInstrumentoRepository;
    private final CirurgiaAgendadaRepository cirurgiaAgendadaRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public LoteService(TenantRepository tenantRepository,
                       UserRepository userRepository,
                       SetorRepository setorRepository,
                       LoteEtiquetaRepository loteEtiquetaRepository,
                       CmeKitFisicoLoteViewRepository kitFisicoLoteViewRepository,
                       KitVersionRepository kitVersionRepository,
                       MovimentacaoCMERepository movimentacaoRepository,
                       IndicadorQuimicoRepository indicadorQuimicoRepository,
                       IndicadorBiologicoRepository indicadorBiologicoRepository,
                       TesteBowieDickRepository testeBowieDickRepository,
                       ProcessoReprocessamentoRepository processoRepository,
                       ProcessoReprocessamentoService processoReprocessamentoService,
                       KitFisicoRepository kitFisicoRepository,
                       KitFisicoInstrumentoRepository kitFisicoInstrumentoRepository,
                       CirurgiaAgendadaRepository cirurgiaAgendadaRepository,
                       TenantScopeGuard tenantScopeGuard) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.setorRepository = setorRepository;
        this.loteEtiquetaRepository = loteEtiquetaRepository;
        this.kitFisicoLoteViewRepository = kitFisicoLoteViewRepository;
        this.kitVersionRepository = kitVersionRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.indicadorQuimicoRepository = indicadorQuimicoRepository;
        this.indicadorBiologicoRepository = indicadorBiologicoRepository;
        this.testeBowieDickRepository = testeBowieDickRepository;
        this.processoRepository = processoRepository;
        this.processoReprocessamentoService = processoReprocessamentoService;
        this.kitFisicoRepository = kitFisicoRepository;
        this.kitFisicoInstrumentoRepository = kitFisicoInstrumentoRepository;
        this.cirurgiaAgendadaRepository = cirurgiaAgendadaRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public SetorDto createSetor(SetorRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        Setor setor = new Setor();
        setor.setTenant(tenant);
        setor.setNome(request.nome());
        setor.setTipo(request.tipo());
        setor.setDescricao(request.descricao());
        Setor saved = setorRepository.save(setor);
        return new SetorDto(saved.getId(), tenant.getId(), saved.getNome(), saved.getTipo(), saved.getDescricao());
    }

    public SetorDto findSetorById(Long id) {
        Setor s = setorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Setor não encontrado"));
        return new SetorDto(s.getId(), s.getTenant().getId(), s.getNome(), s.getTipo(), s.getDescricao());
    }

    public Page<SetorDto> listSetores(Pageable pageable) {
        return setorRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(s -> new SetorDto(s.getId(), s.getTenant().getId(), s.getNome(), s.getTipo(), s.getDescricao()));
    }

    public LoteEtiquetaDto createLote(LoteEtiquetaRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        LoteEtiqueta lote = new LoteEtiqueta();
        lote.setTenant(tenant);
        lote.setCodigo(request.codigo());
        lote.setTipoFluxo(TipoFluxoCME.CIRURGICO);
        if (request.processoId() != null) {
            ProcessoReprocessamento processo = processoRepository.findById(request.processoId())
                    .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado"));
            lote.setProcesso(processo);
        }
        if (request.kitVersaoId() != null) {
            KitVersion versao = kitVersionRepository.findById(request.kitVersaoId())
                    .orElseThrow(() -> new EntityNotFoundException("Versão de kit não encontrada"));
            lote.setKitVersao(versao);
        }
        if (request.kitFisicoId() != null) {
            KitFisico kitFisico = kitFisicoRepository.findById(request.kitFisicoId())
                    .orElseThrow(() -> new EntityNotFoundException("Kit físico não encontrado"));
            tenantScopeGuard.checkTenantAccess(kitFisico.getTenant().getId());
            lote.setKitFisico(kitFisico);
            if (lote.getKitVersao() == null && kitFisico.getKitVersaoAtual() != null) {
                lote.setKitVersao(kitFisico.getKitVersaoAtual());
            }
        }
        LocalDate dataEmpacotamento = request.dataEmpacotamento() != null ? request.dataEmpacotamento() : LocalDate.now();
        lote.setDataEmpacotamento(dataEmpacotamento);
        lote.setValidade(calcularValidadeLote(lote.getKitVersao(), dataEmpacotamento));
        lote.setStatus(request.status() != null ? request.status() : LoteStatus.MONTADO);
        lote.setQrCode(hasText(request.qrCode()) ? request.qrCode().trim() : gerarQrCode(lote.getCodigo()));
        if (request.montadoPorId() != null) {
            User usuario = userRepository.findById(request.montadoPorId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
            lote.setMontadoPor(usuario);
        }
        lote.setDataHoraInicioMontagem(request.dataHoraInicioMontagem());
        lote.setDataHoraFimMontagem(request.dataHoraFimMontagem());
        lote.setObservacoes(request.observacoes());
        return toLoteDto(loteEtiquetaRepository.save(lote));
    }

    public LoteEtiquetaDto registrarEntradaKit(EntradaKitCmeRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        KitFisico kitFisico = kitFisicoRepository.findById(request.kitFisicoId())
                .orElseThrow(() -> new EntityNotFoundException("Kit físico não encontrado"));
        tenantScopeGuard.checkTenantAccess(kitFisico.getTenant().getId());

        if (!Boolean.TRUE.equals(kitFisico.getAtivo())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "kit-fisico.inativo",
                    "O kit físico selecionado está inativo.");
        }
        if (kitFisico.getKitVersaoAtual() == null || kitFisico.getKit() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "kit-fisico.sem-versao",
                    "Associe uma versão válida ao kit físico antes de registrar a entrada na CME.");
        }
        if (kitFisico.getStatusAprovacao() != StatusAprovacaoCme.APROVADO) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "kit-fisico.nao-aprovado",
                    "A composição do kit físico precisa estar aprovada antes de registrar a entrada na CME.");
        }
        long lotesAbertos = loteEtiquetaRepository.countByKitFisico_IdAndTenant_IdAndStatusIn(
                kitFisico.getId(),
                tenant.getId(),
                List.of(LoteStatus.MONTADO, LoteStatus.EM_PROCESSO, LoteStatus.LIBERADO,
                        LoteStatus.DISPONIVEL_ESTOQUE, LoteStatus.BLOQUEADO)
        );
        if (lotesAbertos > 0) {
            throw new ApplicationException(HttpStatus.CONFLICT, "kit-fisico.lote-aberto",
                    "Este kit físico já possui um lote em aberto.");
        }

        LocalDateTime entradaEm = request.entradaEm() != null ? request.entradaEm() : LocalDateTime.now();
        LocalDate dataBase = entradaEm.toLocalDate();
        String codigo = gerarCodigoLote(tenant.getId(), dataBase);

        LoteEtiqueta lote = new LoteEtiqueta();
        lote.setTenant(tenant);
        lote.setCodigo(codigo);
        lote.setTipoFluxo(TipoFluxoCME.CIRURGICO);
        lote.setKitFisico(kitFisico);
        lote.setKitVersao(kitFisico.getKitVersaoAtual());
        lote.setDataEmpacotamento(dataBase);
        lote.setValidade(calcularValidadeLote(lote.getKitVersao(), dataBase));
        lote.setStatus(LoteStatus.EM_PROCESSO);
        lote.setQrCode(gerarQrCode(codigo));
        lote.setDataHoraInicioMontagem(entradaEm);
        if (request.responsavelId() != null) {
            User responsavel = userRepository.findById(request.responsavelId())
                    .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
            lote.setMontadoPor(responsavel);
        }
        lote.setObservacoes(request.observacoes());
        LoteEtiqueta saved = loteEtiquetaRepository.save(lote);

        MovimentacaoCME movimentacao = new MovimentacaoCME();
        movimentacao.setTenant(tenant);
        movimentacao.setLote(saved);
        movimentacao.setTipo(MovimentacaoTipo.ENTRADA_CONTAMINADO);
        movimentacao.setDataHora(entradaEm);
        movimentacao.setResponsavel(saved.getMontadoPor());
        movimentacao.setObservacoes(hasText(request.observacoes()) ? request.observacoes().trim() : "Entrada do kit na CME.");
        movimentacaoRepository.save(movimentacao);

        return toLoteDto(saved);
    }

    private LocalDate calcularValidadeLote(KitVersion kitVersao, LocalDate dataBase) {
        if (kitVersao != null && kitVersao.getValidadeDias() != null && kitVersao.getValidadeDias() > 0) {
            return dataBase.plusDays(kitVersao.getValidadeDias());
        }
        return dataBase;
    }

    private String gerarCodigoLote(Long tenantId, LocalDate dataBase) {
        String prefixo = "CME-" + dataBase.format(DateTimeFormatter.BASIC_ISO_DATE) + "-";
        for (int sequencial = 1; sequencial <= 9999; sequencial++) {
            String codigo = prefixo + String.format("%04d", sequencial);
            if (!loteEtiquetaRepository.existsByTenant_IdAndCodigoIgnoreCase(tenantId, codigo)) {
                return codigo;
            }
        }
        throw new ApplicationException(HttpStatus.CONFLICT, "lote.codigo-esgotado",
                "Não foi possível gerar um número de lote para a data informada.");
    }

    private String gerarQrCode(String codigo) {
        return "CME_LOTE:" + codigo;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public LoteEtiquetaDto findLoteById(Long id) {
        LoteEtiqueta l = loteEtiquetaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));
        return toLoteDto(l);
    }

    public LoteDetalheDto findLoteDetalhe(Long id) {
        LoteEtiqueta lote = loteEtiquetaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));
        tenantScopeGuard.checkRequestedTenant(lote.getTenant().getId());

        ProcessoTimelineDto timeline = lote.getProcesso() != null
                ? processoReprocessamentoService.getTimeline(lote.getProcesso().getId())
                : null;

        List<Long> cicloIds = timeline != null
                ? timeline.ciclosEsterilizacao().stream().map(CicloEsterilizacaoDto::id).toList()
                : (lote.getCicloEsterilizacao() != null ? List.of(lote.getCicloEsterilizacao().getId()) : List.of());

        List<IndicadorQuimicoDto> indicadoresQuimicos = cicloIds.isEmpty() ? List.of()
                : indicadorQuimicoRepository.findAllByCiclo_IdInOrderByIdAsc(cicloIds).stream()
                .map(this::toIndicadorQuimicoDto)
                .toList();
        List<IndicadorBiologicoDto> indicadoresBiologicos = cicloIds.isEmpty() ? List.of()
                : indicadorBiologicoRepository.findAllByCiclo_IdInOrderByIdAsc(cicloIds).stream()
                .map(this::toIndicadorBiologicoDto)
                .toList();

        List<TesteBowieDickDto> testesBowieDick = timeline == null ? List.of()
                : timeline.ciclosEsterilizacao().stream()
                .filter(ciclo -> ciclo.autoclaveId() != null && ciclo.inicio() != null)
                .map(ciclo -> testeBowieDickRepository.findByAutoclave_IdAndDataExecucao(ciclo.autoclaveId(), ciclo.inicio().toLocalDate()).orElse(null))
                .filter(Objects::nonNull)
                .map(this::toTesteBowieDickDto)
                .toList();

        return new LoteDetalheDto(
                toLoteDto(lote),
                lote.getKitFisico() != null ? toKitFisicoDto(lote.getKitFisico()) : null,
                lote.getKitFisico() != null
                        ? kitFisicoInstrumentoRepository.findAllByKitFisico_IdAndAtivoTrueOrderByInstrumentoFisico_IdentificadorUnicoAsc(lote.getKitFisico().getId())
                        .stream().map(this::toKitFisicoInstrumentoDto).toList()
                        : List.of(),
                timeline,
                movimentacaoRepository.findAllByLote_IdOrderByDataHoraAsc(lote.getId()).stream().map(this::toMovimentacaoDto).toList(),
                indicadoresQuimicos,
                indicadoresBiologicos,
                testesBowieDick
        );
    }

    public LoteEtiquetaDto updateLoteStatus(Long id, com.erp.qualitascareapi.cme.enums.LoteStatus status) {
        LoteEtiqueta l = loteEtiquetaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));
        l.setStatus(status);
        return toLoteDto(loteEtiquetaRepository.save(l));
    }

    public BaixaUsoLoteDto registrarBaixaUso(BaixaUsoLoteRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        LoteEtiqueta lote = loteEtiquetaRepository
                .findByTenant_IdAndCodigoIgnoreCase(request.tenantId(), request.codigoLote().trim())
                .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));
        if (lote.getCicloEsterilizacao() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "cme.lote.sem-ciclo-esterilizacao",
                    "O lote informado não possui ciclo de esterilização vinculado para registrar o marcador químico interno.");
        }

        LocalDateTime dataHora = request.dataHora() != null ? request.dataHora() : LocalDateTime.now();
        lote.setStatus(LoteStatus.DISPENSADO);
        LoteEtiqueta savedLote = loteEtiquetaRepository.save(lote);

        MovimentacaoCME movimentacao = new MovimentacaoCME();
        movimentacao.setTenant(savedLote.getTenant());
        movimentacao.setLote(savedLote);
        movimentacao.setTipo(MovimentacaoTipo.RETORNO_CONTAMINADO);
        movimentacao.setDataHora(dataHora);
        if (request.responsavelId() != null) {
            User responsavel = userRepository.findById(request.responsavelId())
                    .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
            movimentacao.setResponsavel(responsavel);
        }
        movimentacao.setObservacoes(request.observacoes());
        MovimentacaoCME savedMovimentacao = movimentacaoRepository.save(movimentacao);

        IndicadorQuimico marcador = new IndicadorQuimico();
        marcador.setCiclo(savedLote.getCicloEsterilizacao());
        marcador.setTipo("MARCADOR_QUIMICO_INTERNO");
        marcador.setResultado(request.resultadoMarcadorQuimicoInterno());
        marcador.setObservacoes(request.observacoes());
        IndicadorQuimico savedMarcador = indicadorQuimicoRepository.save(marcador);
        IndicadorQuimicoDto marcadorDto = new IndicadorQuimicoDto(savedMarcador.getId(), savedMarcador.getCiclo().getId(),
                savedMarcador.getTipo(), savedMarcador.getResultado(), savedMarcador.getObservacoes(), Set.of());

        return new BaixaUsoLoteDto(toLoteDto(savedLote), toMovimentacaoDto(savedMovimentacao), marcadorDto);
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

    private IndicadorQuimicoDto toIndicadorQuimicoDto(IndicadorQuimico i) {
        return new IndicadorQuimicoDto(i.getId(), i.getCiclo().getId(), i.getTipo(), i.getResultado(),
                i.getObservacoes(), toIdSet(i.getEvidencias()));
    }

    private IndicadorBiologicoDto toIndicadorBiologicoDto(IndicadorBiologico i) {
        return new IndicadorBiologicoDto(i.getId(), i.getCiclo().getId(), i.getLoteIndicador(), i.getIncubadora(),
                i.getLeituraEm(), i.getResultado(), i.getObservacoes(), toIdSet(i.getEvidencias()));
    }

    private TesteBowieDickDto toTesteBowieDickDto(TesteBowieDick t) {
        return new TesteBowieDickDto(t.getId(), t.getAutoclave().getId(), t.getDataExecucao(),
                t.getResultado(),
                t.getExecutadoPor() != null ? t.getExecutadoPor().getId() : null,
                t.getExecutadoPor() != null ? t.getExecutadoPor().getFullName() : null,
                t.getValidador() != null ? t.getValidador().getId() : null,
                t.getValidador() != null ? t.getValidador().getFullName() : null,
                t.getStatus(), t.getValidadoEm(), t.getParecerValidacao(), t.getObservacoes(), toIdSet(t.getEvidencias()));
    }

    private KitFisicoDto toKitFisicoDto(KitFisico fisico) {
        KitVersion versao = fisico.getKitVersaoAtual();
        KitProcedimento kit = fisico.getKit();
        return new KitFisicoDto(fisico.getId(), fisico.getTenant().getId(), kit != null ? kit.getId() : null,
                kit != null ? kit.getNome() : null, versao != null ? versao.getId() : null,
                versao != null ? versao.getNumeroVersao() : null,
                fisico.getIdentificadorUnico(), fisico.getStatus(), fisico.getLocalizacao(),
                fisico.getObservacoes(), fisico.getAtivo(), fisico.getStatusAprovacao(),
                fisico.getAprovadoPor() != null ? fisico.getAprovadoPor().getId() : null,
                fisico.getAprovadoPor() != null ? fisico.getAprovadoPor().getFullName() : null,
                fisico.getAprovadoEm(), null, java.util.List.of());
    }

    private KitFisicoInstrumentoDto toKitFisicoInstrumentoDto(KitFisicoInstrumento vinculo) {
        InstrumentoFisico fisico = vinculo.getInstrumentoFisico();
        return new KitFisicoInstrumentoDto(vinculo.getId(), vinculo.getKitFisico().getId(), fisico.getId(),
                fisico.getIdentificadorUnico(), fisico.getInstrumento().getId(), fisico.getInstrumento().getNome(),
                vinculo.getVinculadoEm(), vinculo.getDesvinculadoEm(), vinculo.getObservacoes(), vinculo.getAtivo());
    }

    private Set<Long> toIdSet(Set<com.erp.qualitascareapi.common.domain.EvidenciaArquivo> evidencias) {
        if (evidencias == null) {
            return Set.of();
        }
        return evidencias.stream().map(com.erp.qualitascareapi.common.domain.EvidenciaArquivo::getId).collect(java.util.stream.Collectors.toSet());
    }

    public Page<LoteEtiquetaDto> listLotes(Pageable pageable, Long kitFisicoId, Long requestedTenantId) {
        Long tenantId = effectiveTenantId(requestedTenantId);
        if (kitFisicoId != null) {
            KitFisico kitFisico = kitFisicoRepository.findById(kitFisicoId)
                    .orElseThrow(() -> new EntityNotFoundException("Kit físico não encontrado"));
            tenantScopeGuard.checkTenantAccess(kitFisico.getTenant().getId());
            if (tenantId == null) {
                tenantId = kitFisico.getTenant().getId();
            }
            return kitFisicoLoteViewRepository.findAllByTenantIdAndKitFisicoId(tenantId, kitFisicoId, pageable)
                    .map(this::toLoteDto);
        }
        return loteEtiquetaRepository.findAllByTenant_Id(tenantId, pageable).map(this::toLoteDto);
    }

    public Page<LoteEtiquetaDto> listLotesPorKitFisicoIdentificador(Pageable pageable, String identificador, Long requestedTenantId) {
        Long tenantId = effectiveTenantId(requestedTenantId);
        if (!hasText(identificador)) {
            return Page.empty(pageable);
        }
        return kitFisicoLoteViewRepository
                .findAllByTenantIdAndKitFisicoIdentificadorIgnoreCase(tenantId, identificador.trim(), pageable)
                .map(this::toLoteDto);
    }

    private Long effectiveTenantId(Long requestedTenantId) {
        Long currentTenantId = tenantScopeGuard.currentTenantId();
        if (currentTenantId != null) {
            if (requestedTenantId != null) {
                tenantScopeGuard.checkRequestedTenant(requestedTenantId);
            }
            return currentTenantId;
        }
        tenantScopeGuard.checkRequestedTenant(requestedTenantId);
        return requestedTenantId;
    }

    private LoteEtiquetaDto toLoteDto(CmeKitFisicoLoteView l) {
        return new LoteEtiquetaDto(l.getId(), l.getTenantId(), l.getProcessoId(), l.getCodigo(), l.getKitVersaoId(),
                l.getKitFisicoId(), l.getKitFisicoIdentificador(), l.getDataEmpacotamento(), l.getValidade(),
                l.getStatus(), l.getQrCode(), l.getMontadoPorId(), l.getDataHoraInicioMontagem(),
                l.getDataHoraFimMontagem(), l.getObservacoes(), l.getCriadoEm());
    }

    public MovimentacaoDto registrarMovimentacao(MovimentacaoRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        MovimentacaoCME movimentacao = new MovimentacaoCME();
        movimentacao.setTenant(tenant);
        if (request.loteId() != null) {
            LoteEtiqueta lote = loteEtiquetaRepository.findById(request.loteId())
                    .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));
            movimentacao.setLote(lote);
        }
        if (request.setorOrigemId() != null) {
            Setor origem = setorRepository.findById(request.setorOrigemId())
                    .orElseThrow(() -> new EntityNotFoundException("Setor de origem não encontrado"));
            movimentacao.setSetorOrigem(origem);
        }
        if (request.setorDestinoId() != null) {
            Setor destino = setorRepository.findById(request.setorDestinoId())
                    .orElseThrow(() -> new EntityNotFoundException("Setor de destino não encontrado"));
            movimentacao.setSetorDestino(destino);
        }
        movimentacao.setTipo(request.tipo());
        movimentacao.setDataHora(request.dataHora());
        if (request.responsavelId() != null) {
            User responsavel = userRepository.findById(request.responsavelId())
                    .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
            movimentacao.setResponsavel(responsavel);
        }
        movimentacao.setObservacoes(request.observacoes());

        // Vínculo com cirurgia agendada (MV) — opcional
        if (request.cirurgiaId() != null) {
            CirurgiaAgendada cirurgia = cirurgiaAgendadaRepository.findById(request.cirurgiaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Cirurgia agendada não encontrada: id=" + request.cirurgiaId()));
            // Garante que a cirurgia pertence ao mesmo tenant
            if (!cirurgia.getTenantId().equals(request.tenantId())) {
                throw new IllegalArgumentException(
                        "Cirurgia agendada id=" + request.cirurgiaId() + " não pertence ao tenant informado.");
            }
            movimentacao.setCirurgia(cirurgia);
        }

        MovimentacaoCME saved = movimentacaoRepository.save(movimentacao);
        return toMovimentacaoDto(saved);
    }

    public Page<MovimentacaoDto> listMovimentacoes(Pageable pageable) {
        return movimentacaoRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(this::toMovimentacaoDto);
    }

    private MovimentacaoDto toMovimentacaoDto(MovimentacaoCME m) {
        CirurgiaAgendada cir = m.getCirurgia();
        return new MovimentacaoDto(
                m.getId(),
                m.getTenant().getId(),
                m.getLote() != null ? m.getLote().getId() : null,
                m.getSetorOrigem() != null ? m.getSetorOrigem().getId() : null,
                m.getSetorDestino() != null ? m.getSetorDestino().getId() : null,
                m.getTipo(),
                m.getDataHora(),
                m.getResponsavel() != null ? m.getResponsavel().getId() : null,
                m.getObservacoes(),
                // cirurgia
                cir != null ? cir.getId() : null,
                cir != null ? cir.getCodigoPaciente() : null,
                cir != null ? cir.getNomePaciente() : null,
                cir != null ? cir.getTipoCirurgia() : null,
                cir != null ? cir.getSalaCirurgica() : null,
                cir != null ? cir.getDataHoraInicio() : null
        );
    }
}
