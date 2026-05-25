package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.domain.InstrumentoFisico;
import com.erp.qualitascareapi.cme.domain.KitFisico;
import com.erp.qualitascareapi.cme.domain.KitFisicoInstrumento;
import com.erp.qualitascareapi.cme.enums.IdentificacaoFisicaStatus;
import com.erp.qualitascareapi.cme.enums.LoteStatus;
import com.erp.qualitascareapi.cme.enums.StatusAprovacaoCme;
import com.erp.qualitascareapi.cme.repo.InstrumentoFisicoRepository;
import com.erp.qualitascareapi.cme.repo.KitFisicoInstrumentoRepository;
import com.erp.qualitascareapi.cme.repo.KitFisicoRepository;
import com.erp.qualitascareapi.cme.repo.LoteEtiquetaRepository;
import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.core.domain.Instrumento;
import com.erp.qualitascareapi.core.domain.KitItem;
import com.erp.qualitascareapi.core.domain.KitProcedimento;
import com.erp.qualitascareapi.core.domain.KitVersion;
import com.erp.qualitascareapi.core.repo.InstrumentoRepository;
import com.erp.qualitascareapi.core.repo.KitItemRepository;
import com.erp.qualitascareapi.core.repo.KitProcedimentoRepository;
import com.erp.qualitascareapi.core.repo.KitVersionRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.notificacao.application.NotificacaoService;
import com.erp.qualitascareapi.notificacao.enums.NivelNotificacao;
import com.erp.qualitascareapi.notificacao.enums.TipoNotificacao;
import com.erp.qualitascareapi.notificacao.repo.NotificacaoRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class KitService {

    private static final Logger log = LoggerFactory.getLogger(KitService.class);

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final InstrumentoRepository instrumentoRepository;
    private final KitProcedimentoRepository kitProcedimentoRepository;
    private final KitVersionRepository kitVersionRepository;
    private final KitItemRepository kitItemRepository;
    private final LoteEtiquetaRepository loteEtiquetaRepository;
    private final KitFisicoRepository kitFisicoRepository;
    private final InstrumentoFisicoRepository instrumentoFisicoRepository;
    private final KitFisicoInstrumentoRepository kitFisicoInstrumentoRepository;
    private final TenantScopeGuard tenantScopeGuard;
    private final NotificacaoService notificacaoService;
    private final NotificacaoRepository notificacaoRepository;

    public KitService(TenantRepository tenantRepository,
                      UserRepository userRepository,
                      InstrumentoRepository instrumentoRepository,
                      KitProcedimentoRepository kitProcedimentoRepository,
                      KitVersionRepository kitVersionRepository,
                      KitItemRepository kitItemRepository,
                      LoteEtiquetaRepository loteEtiquetaRepository,
                      KitFisicoRepository kitFisicoRepository,
                      InstrumentoFisicoRepository instrumentoFisicoRepository,
                      KitFisicoInstrumentoRepository kitFisicoInstrumentoRepository,
                      TenantScopeGuard tenantScopeGuard,
                      NotificacaoService notificacaoService,
                      NotificacaoRepository notificacaoRepository) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.instrumentoRepository = instrumentoRepository;
        this.kitProcedimentoRepository = kitProcedimentoRepository;
        this.kitVersionRepository = kitVersionRepository;
        this.kitItemRepository = kitItemRepository;
        this.loteEtiquetaRepository = loteEtiquetaRepository;
        this.kitFisicoRepository = kitFisicoRepository;
        this.instrumentoFisicoRepository = instrumentoFisicoRepository;
        this.kitFisicoInstrumentoRepository = kitFisicoInstrumentoRepository;
        this.tenantScopeGuard = tenantScopeGuard;
        this.notificacaoService = notificacaoService;
        this.notificacaoRepository = notificacaoRepository;
    }

    public InstrumentoDto createInstrumento(InstrumentoRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        Instrumento instrumento = new Instrumento();
        instrumento.setTenant(tenant);
        instrumento.setNome(request.nome());
        instrumento.setCodigoHospitalar(request.codigoHospitalar());
        instrumento.setDescricao(request.descricao());
        Instrumento saved = instrumentoRepository.save(instrumento);
        return new InstrumentoDto(saved.getId(), tenant.getId(), saved.getNome(),
                saved.getCodigoHospitalar(), saved.getDescricao());
    }

    public InstrumentoDto findInstrumentoById(Long id) {
        Instrumento i = instrumentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instrumento não encontrado"));
        return new InstrumentoDto(i.getId(), i.getTenant().getId(), i.getNome(),
                i.getCodigoHospitalar(), i.getDescricao());
    }

    public InstrumentoDto updateInstrumento(Long id, InstrumentoRequest request) {
        Instrumento instrumento = instrumentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instrumento não encontrado"));
        instrumento.setNome(request.nome());
        instrumento.setCodigoHospitalar(request.codigoHospitalar());
        instrumento.setDescricao(request.descricao());
        Instrumento saved = instrumentoRepository.save(instrumento);
        return new InstrumentoDto(saved.getId(), saved.getTenant().getId(), saved.getNome(),
                saved.getCodigoHospitalar(), saved.getDescricao());
    }

    public Page<InstrumentoDto> listInstrumentos(Pageable pageable) {
        return instrumentoRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(i -> new InstrumentoDto(i.getId(), i.getTenant().getId(), i.getNome(),
                        i.getCodigoHospitalar(), i.getDescricao()));
    }

    public KitProcedimentoDto createKitProcedimento(KitProcedimentoRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        KitProcedimento kit = new KitProcedimento();
        kit.setTenant(tenant);
        kit.setNome(request.nome());
        kit.setCodigo(request.codigo());
        kit.setObservacoes(request.observacoes());
        kit.setAtivo(request.ativo() != null ? request.ativo() : Boolean.TRUE);
        KitProcedimento saved = kitProcedimentoRepository.save(kit);
        return toKitProcedimentoDto(saved);
    }

    public KitProcedimentoDto findKitById(Long id) {
        KitProcedimento k = kitProcedimentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kit não encontrado"));
        return toKitProcedimentoDto(k);
    }

    public KitProcedimentoDto updateKit(Long id, KitProcedimentoRequest request) {
        KitProcedimento kit = kitProcedimentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kit não encontrado"));
        kit.setNome(request.nome());
        kit.setCodigo(request.codigo());
        kit.setObservacoes(request.observacoes());
        if (request.ativo() != null) kit.setAtivo(request.ativo());
        KitProcedimento saved = kitProcedimentoRepository.save(kit);
        return toKitProcedimentoDto(saved);
    }

    public Page<KitProcedimentoDto> listKits(Pageable pageable) {
        return kitProcedimentoRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(this::toKitProcedimentoDto);
    }

    public KitVersionDto createKitVersion(KitVersionRequest request) {
        KitProcedimento kit = kitProcedimentoRepository.findById(request.kitId())
                .orElseThrow(() -> new EntityNotFoundException("Kit não encontrado"));
        tenantScopeGuard.checkTenantAccess(kit.getTenant().getId());
        KitVersion version = new KitVersion();
        version.setKit(kit);
        version.setNumeroVersao(request.numeroVersao());
        version.setVigenciaInicio(request.vigenciaInicio());
        version.setValidadeDias(request.validadeDias());
        version.setAtivo(request.ativo() != null ? request.ativo() : Boolean.TRUE);
        version.setStatusAprovacao(StatusAprovacaoCme.PENDENTE);
        version.setObservacoes(request.observacoes());
        KitVersion saved = kitVersionRepository.save(version);
        notificarAprovacaoVersao(saved);
        return toKitVersionDto(saved);
    }

    public Page<KitVersionDto> listKitVersions(Long kitId, Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        Page<KitVersion> versions = kitId == null
                ? kitVersionRepository.findAllByKit_TenantId(tenantId, pageable)
                : kitVersionRepository.findAllByKit_IdAndKit_TenantId(kitId, tenantId, pageable);
        return versions.map(this::toKitVersionDto);
    }

    public KitVersionDto updateKitVersion(Long id, KitVersionRequest request) {
        KitVersion version = kitVersionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Versão de kit não encontrada"));
        tenantScopeGuard.checkTenantAccess(version.getKit().getTenant().getId());
        ensureVersionDataEditable(version);

        version.setNumeroVersao(request.numeroVersao());
        version.setVigenciaInicio(request.vigenciaInicio());
        version.setValidadeDias(request.validadeDias());
        version.setAtivo(request.ativo() != null ? request.ativo() : Boolean.TRUE);
        version.setObservacoes(request.observacoes());
        return toKitVersionDto(kitVersionRepository.save(version));
    }

    public void deleteKitVersion(Long id) {
        KitVersion version = kitVersionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Versão de kit não encontrada"));
        Long tenantId = tenantScopeGuard.currentTenantId();
        tenantScopeGuard.checkTenantAccess(version.getKit().getTenant().getId());

        long itemCount = kitItemRepository.countByVersao_IdAndVersao_Kit_TenantId(id, tenantId);
        if (itemCount > 0) {
            throw new ApplicationException(
                    HttpStatus.CONFLICT,
                    "kit-version.has-items",
                    "A versão possui instrumentos vinculados e não pode ser excluída."
            );
        }

        long loteCount = loteEtiquetaRepository.countByKitVersao_IdAndTenant_Id(id, tenantId);
        if (loteCount > 0) {
            throw new ApplicationException(
                    HttpStatus.CONFLICT,
                    "kit-version.in-use",
                    "A versão está vinculada a lote/etiqueta e não pode ser excluída."
            );
        }

        kitVersionRepository.delete(version);
    }

    public KitItemDto createKitItem(KitItemRequest request) {
        KitVersion versao = kitVersionRepository.findById(request.versaoId())
                .orElseThrow(() -> new EntityNotFoundException("Versão de kit não encontrada"));
        tenantScopeGuard.checkTenantAccess(versao.getKit().getTenant().getId());
        ensureVersionCompositionEditable(versao);
        Instrumento instrumento = instrumentoRepository.findById(request.instrumentoId())
                .orElseThrow(() -> new EntityNotFoundException("Instrumento não encontrado"));
        tenantScopeGuard.checkTenantAccess(instrumento.getTenant().getId());
        KitItem item = new KitItem();
        item.setVersao(versao);
        item.setInstrumento(instrumento);
        item.setQuantidade(request.quantidade());
        item.setObservacoes(request.observacoes());
        KitItem saved = kitItemRepository.save(item);
        return new KitItemDto(saved.getId(), versao.getId(), instrumento.getId(), saved.getQuantidade(),
                saved.getObservacoes());
    }

    public Page<KitItemDto> listKitItems(Long versaoId, Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        Page<KitItem> items = versaoId == null
                ? kitItemRepository.findAllByVersao_Kit_TenantId(tenantId, pageable)
                : kitItemRepository.findAllByVersao_IdAndVersao_Kit_TenantId(versaoId, tenantId, pageable);
        return items
                .map(item -> new KitItemDto(item.getId(), item.getVersao().getId(),
                        item.getInstrumento().getId(), item.getQuantidade(), item.getObservacoes()));
    }

    public KitItemDto updateKitItem(Long id, KitItemRequest request) {
        KitItem item = kitItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item do kit não encontrado"));
        tenantScopeGuard.checkTenantAccess(item.getVersao().getKit().getTenant().getId());
        ensureVersionCompositionEditable(item.getVersao());
        Instrumento instrumento = instrumentoRepository.findById(request.instrumentoId())
                .orElseThrow(() -> new EntityNotFoundException("Instrumento não encontrado"));
        tenantScopeGuard.checkTenantAccess(instrumento.getTenant().getId());
        item.setInstrumento(instrumento);
        item.setQuantidade(request.quantidade());
        item.setObservacoes(request.observacoes());
        KitItem saved = kitItemRepository.save(item);
        return new KitItemDto(saved.getId(), saved.getVersao().getId(), saved.getInstrumento().getId(),
                saved.getQuantidade(), saved.getObservacoes());
    }

    public void deleteKitItem(Long id) {
        KitItem item = kitItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item do kit não encontrado"));
        tenantScopeGuard.checkTenantAccess(item.getVersao().getKit().getTenant().getId());
        ensureVersionCompositionEditable(item.getVersao());
        kitItemRepository.delete(item);
    }

    public KitVersionDto registrarAprovacaoVersao(Long id, KitVersionApprovalRequest request) {
        KitVersion version = kitVersionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Versão de kit não encontrada"));
        tenantScopeGuard.checkTenantAccess(version.getKit().getTenant().getId());
        version.setStatusAprovacao(request.status());
        if (request.status() == StatusAprovacaoCme.APROVADO) {
            User aprovador = request.aprovadoPorId() != null
                    ? userRepository.findById(request.aprovadoPorId()).orElseThrow(() -> new EntityNotFoundException("Usuário aprovador não encontrado"))
                    : null;
            if (aprovador != null) {
                tenantScopeGuard.checkTenantAccess(aprovador.getTenant().getId());
            }
            version.setAprovadoPor(aprovador);
            version.setAprovadoEm(LocalDateTime.now());
        } else if (request.status() == StatusAprovacaoCme.REPROVADO) {
            version.setAprovadoPor(null);
            version.setAprovadoEm(null);
        }
        if (request.comentario() != null && !request.comentario().isBlank()) {
            version.setObservacoes(request.comentario());
        }
        return toKitVersionDto(kitVersionRepository.save(version));
    }

    public KitVersionDto revalidarVersao(Long id, KitVersionRevalidacaoRequest request) {
        KitVersion version = kitVersionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Versão de kit não encontrada"));
        tenantScopeGuard.checkTenantAccess(version.getKit().getTenant().getId());
        version.setValidadeDias(request.validadeDias());
        version.setRevalidadoEm(LocalDateTime.now());
        if (request.observacoes() != null && !request.observacoes().isBlank()) {
            version.setObservacoes(request.observacoes());
        }
        return toKitVersionDto(kitVersionRepository.save(version));
    }

    private void ensureVersionCompositionEditable(KitVersion version) {
        if (version.getStatusAprovacao() == StatusAprovacaoCme.APROVADO) {
            throw new ApplicationException(HttpStatus.CONFLICT, "kit-version.composicao-aprovada",
                    "A composição só pode ser alterada enquanto a versão não estiver aprovada pela responsável da unidade.");
        }
    }

    private void ensureVersionDataEditable(KitVersion version) {
        if (version.getStatusAprovacao() == StatusAprovacaoCme.APROVADO) {
            throw new ApplicationException(HttpStatus.CONFLICT, "kit-version.aprovada",
                    "Os dados da versão só podem ser alterados enquanto ela não estiver aprovada.");
        }
    }

    private KitProcedimentoDto toKitProcedimentoDto(KitProcedimento kit) {
        KitVersion versaoValida = findVersaoValida(kit.getId());
        return new KitProcedimentoDto(kit.getId(), kit.getTenant().getId(), kit.getNome(), kit.getCodigo(),
                kit.getObservacoes(), kit.getAtivo(),
                versaoValida != null ? versaoValida.getId() : null,
                versaoValida != null ? versaoValida.getNumeroVersao() : null);
    }

    private KitVersionDto toKitVersionDto(KitVersion v) {
        User aprovador = v.getAprovadoPor();
        KitVersion versaoValida = findVersaoValida(v.getKit().getId());
        return new KitVersionDto(v.getId(), v.getKit().getId(), v.getNumeroVersao(),
                v.getVigenciaInicio(), v.getValidadeDias(), v.getAtivo(), v.getObservacoes(),
                v.getStatusAprovacao() != null ? v.getStatusAprovacao() : StatusAprovacaoCme.PENDENTE,
                aprovador != null ? aprovador.getId() : null,
                aprovador != null ? aprovador.getFullName() : null, v.getAprovadoEm(), v.getRevalidadoEm(),
                versaoValida != null && versaoValida.getId().equals(v.getId()));
    }

    private KitVersion findVersaoValida(Long kitId) {
        return kitVersionRepository
                .findAllByKit_IdAndAtivoTrueAndStatusAprovacao(kitId, StatusAprovacaoCme.APROVADO)
                .stream()
                .filter(this::isVersaoEmPeriodo)
                .max(Comparator
                        .comparing(KitVersion::getVigenciaInicio, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(KitVersion::getNumeroVersao, Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElse(null);
    }

    private boolean isVersaoEmPeriodo(KitVersion version) {
        if (!Boolean.TRUE.equals(version.getAtivo()) || version.getStatusAprovacao() != StatusAprovacaoCme.APROVADO) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate inicio = version.getVigenciaInicio();
        if (inicio != null && inicio.isAfter(today)) {
            return false;
        }
        if (inicio != null && version.getValidadeDias() != null) {
            LocalDate fim = inicio.plusDays(version.getValidadeDias());
            return !fim.isBefore(today);
        }
        return true;
    }

    private void ensureKitFisicoVersionUsable(KitVersion version) {
        KitVersion versaoValida = findVersaoValida(version.getKit().getId());
        if (versaoValida == null || !versaoValida.getId().equals(version.getId())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "kit-fisico.versao-nao-valida",
                    "Selecione uma versão aprovada, ativa e vigente para cadastrar o kit físico.");
        }
    }

    private void notificarAprovacaoVersao(KitVersion version) {
        Long tenantId = version.getKit().getTenant().getId();
        Long versionId = version.getId();
        Integer numeroVersao = version.getNumeroVersao();
        String kitNome = version.getKit().getNome();
        String titulo = "Versão de kit pendente de aprovação";
        String mensagem = "A versão v" + numeroVersao + " do kit " + kitNome
                + " foi cadastrada e precisa ser aprovada pela supervisora da CME.";

        try {
            List<User> supervisores = userRepository.findCmeSupervisorsForKitApproval(tenantId);
            if (supervisores.isEmpty()) {
                log.warn("Nenhuma supervisora CME encontrada para aprovação da versão de kit {} no tenant {}.",
                        versionId, tenantId);
                notificacaoService.gerar(
                        tenantId,
                        TipoNotificacao.CME_KIT_VERSAO_APROVACAO_SOLICITADA,
                        NivelNotificacao.INFO,
                        titulo,
                        mensagem,
                        versionId,
                        "CME_KIT_VERSION"
                );
                return;
            }

            for (User supervisor : supervisores) {
                notificacaoService.gerar(
                        tenantId,
                        TipoNotificacao.CME_KIT_VERSAO_APROVACAO_SOLICITADA,
                        NivelNotificacao.INFO,
                        titulo,
                        mensagem,
                        versionId,
                        "CME_KIT_VERSION",
                        supervisor.getId()
                );
            }
        } catch (RuntimeException ex) {
            log.error("Não foi possível gerar notificação de aprovação da versão de kit {}.", versionId, ex);
            throw new ApplicationException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "kit-version.notification-failed",
                    "Não foi possível gerar a notificação de aprovação da versão do kit.",
                    ex
            );
        }
    }

    public InstrumentoFisicoDto createInstrumentoFisico(InstrumentoFisicoRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        if (instrumentoFisicoRepository.existsByTenant_IdAndIdentificadorUnicoIgnoreCase(request.tenantId(), request.identificadorUnico().trim())) {
            throw new ApplicationException(HttpStatus.CONFLICT, "instrumento-fisico.identificador-duplicado",
                    "Já existe um instrumento físico com este identificador.");
        }
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        Instrumento instrumento = instrumentoRepository.findById(request.instrumentoId())
                .orElseThrow(() -> new EntityNotFoundException("Instrumento não encontrado"));
        tenantScopeGuard.checkTenantAccess(instrumento.getTenant().getId());

        InstrumentoFisico fisico = new InstrumentoFisico();
        fisico.setTenant(tenant);
        fisico.setInstrumento(instrumento);
        fisico.setIdentificadorUnico(request.identificadorUnico().trim());
        fisico.setStatus(request.status() != null ? request.status() : IdentificacaoFisicaStatus.ATIVO);
        fisico.setLocalizacao(request.localizacao());
        fisico.setObservacoes(request.observacoes());
        fisico.setAtivo(request.ativo() != null ? request.ativo() : Boolean.TRUE);
        return toInstrumentoFisicoDto(instrumentoFisicoRepository.save(fisico));
    }

    public Page<InstrumentoFisicoDto> listInstrumentosFisicos(Pageable pageable) {
        return instrumentoFisicoRepository.findAllByTenant_Id(tenantScopeGuard.currentTenantId(), pageable)
                .map(this::toInstrumentoFisicoDto);
    }

    public InstrumentoFisicoDto findInstrumentoFisicoById(Long id) {
        InstrumentoFisico fisico = instrumentoFisicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instrumento físico não encontrado"));
        tenantScopeGuard.checkTenantAccess(fisico.getTenant().getId());
        return toInstrumentoFisicoDto(fisico);
    }

    public InstrumentoFisicoDto updateInstrumentoFisico(Long id, InstrumentoFisicoRequest request) {
        InstrumentoFisico fisico = instrumentoFisicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instrumento físico não encontrado"));
        tenantScopeGuard.checkTenantAccess(fisico.getTenant().getId());
        instrumentoFisicoRepository
                .findByTenant_IdAndIdentificadorUnicoIgnoreCase(fisico.getTenant().getId(), request.identificadorUnico().trim())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ApplicationException(HttpStatus.CONFLICT, "instrumento-fisico.identificador-duplicado",
                            "Já existe um instrumento físico com este identificador.");
                });
        Instrumento instrumento = instrumentoRepository.findById(request.instrumentoId())
                .orElseThrow(() -> new EntityNotFoundException("Instrumento não encontrado"));
        tenantScopeGuard.checkTenantAccess(instrumento.getTenant().getId());

        fisico.setInstrumento(instrumento);
        fisico.setIdentificadorUnico(request.identificadorUnico().trim());
        fisico.setStatus(request.status() != null ? request.status() : IdentificacaoFisicaStatus.ATIVO);
        fisico.setLocalizacao(request.localizacao());
        fisico.setObservacoes(request.observacoes());
        fisico.setAtivo(request.ativo() != null ? request.ativo() : Boolean.TRUE);
        return toInstrumentoFisicoDto(instrumentoFisicoRepository.save(fisico));
    }

    public KitFisicoDto createKitFisico(KitFisicoRequest request) {
        Long tenantId = effectiveTenantId(request.tenantId());
        if (kitFisicoRepository.existsByTenant_IdAndIdentificadorUnicoIgnoreCase(tenantId, request.identificadorUnico().trim())) {
            throw new ApplicationException(HttpStatus.CONFLICT, "kit-fisico.identificador-duplicado",
                    "Já existe um kit físico com este identificador.");
        }
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        KitProcedimento kit = null;
        if (request.kitId() != null) {
            kit = kitProcedimentoRepository.findById(request.kitId())
                    .orElseThrow(() -> new EntityNotFoundException("Kit não encontrado"));
            tenantScopeGuard.checkTenantAccess(kit.getTenant().getId());
        }
        KitVersion versaoAtual = null;
        if (request.kitVersaoAtualId() != null) {
            versaoAtual = kitVersionRepository.findById(request.kitVersaoAtualId())
                    .orElseThrow(() -> new EntityNotFoundException("Versão de kit não encontrada"));
            if (kit == null) {
                kit = versaoAtual.getKit();
                tenantScopeGuard.checkTenantAccess(kit.getTenant().getId());
            } else if (!versaoAtual.getKit().getId().equals(kit.getId())) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "kit-fisico.versao-invalida",
                        "A versão informada não pertence ao kit selecionado.");
            }
            ensureKitFisicoVersionUsable(versaoAtual);
        }

        KitFisico fisico = new KitFisico();
        fisico.setTenant(tenant);
        fisico.setKit(kit);
        fisico.setKitVersaoAtual(versaoAtual);
        fisico.setIdentificadorUnico(request.identificadorUnico().trim());
        fisico.setStatus(request.status() != null ? request.status() : IdentificacaoFisicaStatus.ATIVO);
        fisico.setLocalizacao(request.localizacao());
        fisico.setObservacoes(request.observacoes());
        fisico.setAtivo(request.ativo() != null ? request.ativo() : Boolean.TRUE);
        fisico.setStatusAprovacao(StatusAprovacaoCme.PENDENTE);
        return toKitFisicoDto(kitFisicoRepository.save(fisico));
    }

    public Page<KitFisicoDto> listKitsFisicos(Pageable pageable, String identificador, Long requestedTenantId) {
        Long tenantId = effectiveTenantId(requestedTenantId);
        if (identificador != null && !identificador.isBlank()) {
            return kitFisicoRepository
                    .findAllByTenant_IdAndIdentificadorUnicoContainingIgnoreCase(tenantId, identificador.trim(), pageable)
                    .map(this::toKitFisicoDto);
        }
        return kitFisicoRepository.findAllByTenant_Id(tenantId, pageable).map(this::toKitFisicoDto);
    }

    public Page<KitFisicoDto> listKitsFisicosDisponiveisEntrada(Pageable pageable, String identificador, Long requestedTenantId) {
        Long tenantId = effectiveTenantId(requestedTenantId);
        return kitFisicoRepository.findDisponiveisEntrada(
                tenantId,
                identificador != null && !identificador.isBlank() ? identificador.trim() : null,
                IdentificacaoFisicaStatus.ATIVO,
                StatusAprovacaoCme.APROVADO,
                List.of(LoteStatus.MONTADO, LoteStatus.EM_PROCESSO, LoteStatus.LIBERADO,
                        LoteStatus.DISPONIVEL_ESTOQUE, LoteStatus.BLOQUEADO),
                pageable
        ).map(this::toKitFisicoDto);
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

    public KitFisicoDto findKitFisicoById(Long id) {
        KitFisico fisico = kitFisicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kit físico não encontrado"));
        tenantScopeGuard.checkTenantAccess(fisico.getTenant().getId());
        return toKitFisicoDto(fisico);
    }

    public KitFisicoDto updateKitFisico(Long id, KitFisicoRequest request) {
        KitFisico fisico = kitFisicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kit físico não encontrado"));
        tenantScopeGuard.checkTenantAccess(fisico.getTenant().getId());
        kitFisicoRepository
                .findByTenant_IdAndIdentificadorUnicoIgnoreCase(fisico.getTenant().getId(), request.identificadorUnico().trim())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ApplicationException(HttpStatus.CONFLICT, "kit-fisico.identificador-duplicado",
                            "Já existe um kit físico com este identificador.");
                });
        KitProcedimento kit = null;
        if (request.kitId() != null) {
            kit = kitProcedimentoRepository.findById(request.kitId())
                    .orElseThrow(() -> new EntityNotFoundException("Kit não encontrado"));
            tenantScopeGuard.checkTenantAccess(kit.getTenant().getId());
        }
        Long versaoAtualId = fisico.getKitVersaoAtual() != null ? fisico.getKitVersaoAtual().getId() : null;
        Long kitAtualId = fisico.getKit() != null ? fisico.getKit().getId() : null;
        Long novoKitId = kit != null ? kit.getId() : null;
        if (!java.util.Objects.equals(kitAtualId, novoKitId)
                || !java.util.Objects.equals(versaoAtualId, request.kitVersaoAtualId())) {
            ensureKitFisicoWithoutOpenSterilizationLot(fisico);
        }
        KitVersion versaoAtual = null;
        if (request.kitVersaoAtualId() != null) {
            versaoAtual = kitVersionRepository.findById(request.kitVersaoAtualId())
                    .orElseThrow(() -> new EntityNotFoundException("Versão de kit não encontrada"));
            if (kit == null) {
                kit = versaoAtual.getKit();
                tenantScopeGuard.checkTenantAccess(kit.getTenant().getId());
            } else if (!versaoAtual.getKit().getId().equals(kit.getId())) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "kit-fisico.versao-invalida",
                        "A versão informada não pertence ao kit selecionado.");
            }
            ensureKitFisicoVersionUsable(versaoAtual);
        }

        fisico.setKit(kit);
        fisico.setKitVersaoAtual(versaoAtual);
        fisico.setIdentificadorUnico(request.identificadorUnico().trim());
        fisico.setStatus(request.status() != null ? request.status() : IdentificacaoFisicaStatus.ATIVO);
        fisico.setLocalizacao(request.localizacao());
        fisico.setObservacoes(request.observacoes());
        fisico.setAtivo(request.ativo() != null ? request.ativo() : Boolean.TRUE);
        fisico.setStatusAprovacao(StatusAprovacaoCme.PENDENTE);
        fisico.setAprovadoPor(null);
        fisico.setAprovadoEm(null);
        KitFisico saved = kitFisicoRepository.save(fisico);
        notificarAprovacaoKitFisicoSeComposicaoConforme(saved);
        return toKitFisicoDto(saved);
    }

    public KitFisicoDto registrarAprovacaoKitFisico(Long id, KitVersionApprovalRequest request) {
        KitFisico fisico = kitFisicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kit físico não encontrado"));
        tenantScopeGuard.checkTenantAccess(fisico.getTenant().getId());
        if (request.status() == StatusAprovacaoCme.APROVADO) {
            List<String> pendencias = verificarPendenciasComposicao(fisico);
            if (!pendencias.isEmpty()) {
                throw new ApplicationException(HttpStatus.CONFLICT, "kit-fisico.composicao-incompleta",
                        "O kit físico ainda não contempla a composição exigida pelo modelo.");
            }
            User aprovador = request.aprovadoPorId() != null
                    ? userRepository.findById(request.aprovadoPorId()).orElseThrow(() -> new EntityNotFoundException("Usuário aprovador não encontrado"))
                    : null;
            if (aprovador != null) {
                tenantScopeGuard.checkTenantAccess(aprovador.getTenant().getId());
            }
            fisico.setAprovadoPor(aprovador);
            fisico.setAprovadoEm(LocalDateTime.now());
        } else {
            fisico.setAprovadoPor(null);
            fisico.setAprovadoEm(null);
        }
        fisico.setStatusAprovacao(request.status());
        if (request.comentario() != null && !request.comentario().isBlank()) {
            fisico.setObservacoes(request.comentario());
        }
        return toKitFisicoDto(kitFisicoRepository.save(fisico));
    }

    public KitFisicoInstrumentoDto vincularInstrumentoFisico(KitFisicoInstrumentoRequest request) {
        KitFisico kitFisico = kitFisicoRepository.findById(request.kitFisicoId())
                .orElseThrow(() -> new EntityNotFoundException("Kit físico não encontrado"));
        InstrumentoFisico instrumentoFisico = instrumentoFisicoRepository.findById(request.instrumentoFisicoId())
                .orElseThrow(() -> new EntityNotFoundException("Instrumento físico não encontrado"));
        tenantScopeGuard.checkTenantAccess(kitFisico.getTenant().getId());
        tenantScopeGuard.checkTenantAccess(instrumentoFisico.getTenant().getId());
        ensureKitFisicoWithoutOpenSterilizationLot(kitFisico);
        if (kitFisico.getKitVersaoAtual() == null) {
            throw new ApplicationException(HttpStatus.CONFLICT, "kit-fisico.modelo-nao-configurado",
                    "Associe uma versão válida de modelo ao kit físico antes de montar a composição.");
        }

        kitFisicoInstrumentoRepository
                .findByKitFisico_IdAndInstrumentoFisico_IdAndAtivoTrue(kitFisico.getId(), instrumentoFisico.getId())
                .ifPresent(existing -> {
                    throw new ApplicationException(HttpStatus.CONFLICT, "kit-fisico.instrumento-ja-vinculado",
                            "Este instrumento físico já está vinculado ao kit físico.");
                });
        kitFisicoInstrumentoRepository.findByInstrumentoFisico_IdAndAtivoTrue(instrumentoFisico.getId())
                .ifPresent(existing -> {
                    throw new ApplicationException(HttpStatus.CONFLICT, "instrumento-fisico.vinculado-outro-kit",
                            "Este instrumento físico já compõe outro kit. Remova-o do kit atual antes de vinculá-lo novamente.");
                });

        KitFisicoInstrumento vinculo = new KitFisicoInstrumento();
        vinculo.setKitFisico(kitFisico);
        vinculo.setInstrumentoFisico(instrumentoFisico);
        if (request.vinculadoEm() != null) {
            vinculo.setVinculadoEm(request.vinculadoEm());
        }
        vinculo.setObservacoes(request.observacoes());
        KitFisicoInstrumento saved = kitFisicoInstrumentoRepository.save(vinculo);
        kitFisico.setStatusAprovacao(StatusAprovacaoCme.PENDENTE);
        kitFisico.setAprovadoPor(null);
        kitFisico.setAprovadoEm(null);
        notificarAprovacaoKitFisicoSeComposicaoConforme(kitFisico);
        return toKitFisicoInstrumentoDto(saved);
    }

    public Page<KitFisicoInstrumentoDto> listInstrumentosDoKitFisico(Long kitFisicoId, Pageable pageable) {
        KitFisico kitFisico = kitFisicoRepository.findById(kitFisicoId)
                .orElseThrow(() -> new EntityNotFoundException("Kit físico não encontrado"));
        tenantScopeGuard.checkTenantAccess(kitFisico.getTenant().getId());
        java.util.List<KitFisicoInstrumentoDto> instrumentos = kitFisicoInstrumentoRepository
                .findAllByKitFisico_IdAndAtivoTrueOrderByInstrumentoFisico_IdentificadorUnicoAsc(kitFisicoId)
                .stream().map(this::toKitFisicoInstrumentoDto).toList();
        return new org.springframework.data.domain.PageImpl<>(
                instrumentos,
                pageable,
                instrumentos.size()
        );
    }

    public void desvincularInstrumentoFisico(Long vinculoId) {
        KitFisicoInstrumento vinculo = kitFisicoInstrumentoRepository.findById(vinculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo de composição não encontrado"));
        tenantScopeGuard.checkTenantAccess(vinculo.getKitFisico().getTenant().getId());
        ensureKitFisicoWithoutOpenSterilizationLot(vinculo.getKitFisico());
        vinculo.setAtivo(Boolean.FALSE);
        vinculo.setDesvinculadoEm(LocalDate.now());
        vinculo.getKitFisico().setStatusAprovacao(StatusAprovacaoCme.PENDENTE);
        vinculo.getKitFisico().setAprovadoPor(null);
        vinculo.getKitFisico().setAprovadoEm(null);
        kitFisicoInstrumentoRepository.save(vinculo);
    }

    private void ensureKitFisicoWithoutOpenSterilizationLot(KitFisico fisico) {
        long lotesAbertos = loteEtiquetaRepository.countByKitFisico_IdAndTenant_IdAndStatusIn(
                fisico.getId(),
                fisico.getTenant().getId(),
                List.of(LoteStatus.MONTADO, LoteStatus.EM_PROCESSO)
        );
        if (lotesAbertos > 0) {
            throw new ApplicationException(HttpStatus.CONFLICT, "kit-fisico.lote-aberto",
                    "O kit físico possui lote de esterilização em aberto e não pode ser ajustado agora.");
        }
    }

    private List<String> verificarPendenciasComposicao(KitFisico fisico) {
        KitVersion versao = fisico.getKitVersaoAtual();
        if (versao == null) {
            return List.of("Selecione uma versão válida do modelo do kit.");
        }

        Map<Long, Integer> exigido = new HashMap<>();
        Map<Long, String> nomes = new HashMap<>();
        for (KitItem item : kitItemRepository.findAllByVersao_Id(versao.getId())) {
            Long instrumentoId = item.getInstrumento().getId();
            exigido.merge(instrumentoId, item.getQuantidade(), Integer::sum);
            nomes.put(instrumentoId, item.getInstrumento().getNome());
        }

        Map<Long, Integer> atual = new HashMap<>();
        for (KitFisicoInstrumento vinculo : kitFisicoInstrumentoRepository
                .findAllByKitFisico_IdAndAtivoTrueOrderByInstrumentoFisico_IdentificadorUnicoAsc(fisico.getId())) {
            Long instrumentoId = vinculo.getInstrumentoFisico().getInstrumento().getId();
            atual.merge(instrumentoId, 1, Integer::sum);
            nomes.putIfAbsent(instrumentoId, vinculo.getInstrumentoFisico().getInstrumento().getNome());
        }

        List<String> pendencias = new ArrayList<>();
        for (Map.Entry<Long, Integer> item : exigido.entrySet()) {
            int quantidadeAtual = atual.getOrDefault(item.getKey(), 0);
            if (quantidadeAtual < item.getValue()) {
                pendencias.add("Falta " + (item.getValue() - quantidadeAtual) + "x " + nomes.get(item.getKey()));
            }
        }
        for (Map.Entry<Long, Integer> item : atual.entrySet()) {
            int quantidadeExigida = exigido.getOrDefault(item.getKey(), 0);
            if (quantidadeExigida == 0) {
                pendencias.add("Instrumento não previsto: " + nomes.get(item.getKey()) + " (" + item.getValue() + "x)");
            } else if (item.getValue() > quantidadeExigida) {
                pendencias.add("Excesso de " + (item.getValue() - quantidadeExigida) + "x " + nomes.get(item.getKey()));
            }
        }
        return pendencias;
    }

    private void notificarAprovacaoKitFisicoSeComposicaoConforme(KitFisico fisico) {
        List<String> pendencias = verificarPendenciasComposicao(fisico);
        if (!pendencias.isEmpty()) {
            return;
        }
        Long tenantId = fisico.getTenant().getId();
        String titulo = "Kit físico pronto para aprovação";
        String mensagem = "O kit físico " + fisico.getIdentificadorUnico() + " contempla a composição do modelo "
                + (fisico.getKit() != null ? fisico.getKit().getNome() : "selecionado") + " e precisa ser aprovado pela supervisora da CME.";
        List<User> supervisores = userRepository.findCmeSupervisorsForKitApproval(tenantId);
        if (supervisores.isEmpty()) {
            if (!notificacaoRepository.existsByTipoAndReferenciaTipoAndReferenciaIdAndUsuarioIdAndLidaFalse(
                    TipoNotificacao.CME_KIT_FISICO_APROVACAO_SOLICITADA, "CME_KIT_FISICO", fisico.getId(), null)) {
                notificacaoService.gerar(tenantId, TipoNotificacao.CME_KIT_FISICO_APROVACAO_SOLICITADA,
                        NivelNotificacao.INFO, titulo, mensagem, fisico.getId(), "CME_KIT_FISICO");
            }
            return;
        }
        for (User supervisor : supervisores) {
            if (!notificacaoRepository.existsByTipoAndReferenciaTipoAndReferenciaIdAndUsuarioIdAndLidaFalse(
                    TipoNotificacao.CME_KIT_FISICO_APROVACAO_SOLICITADA, "CME_KIT_FISICO", fisico.getId(), supervisor.getId())) {
                notificacaoService.gerar(tenantId, TipoNotificacao.CME_KIT_FISICO_APROVACAO_SOLICITADA,
                        NivelNotificacao.INFO, titulo, mensagem, fisico.getId(), "CME_KIT_FISICO", supervisor.getId());
            }
        }
    }

    private InstrumentoFisicoDto toInstrumentoFisicoDto(InstrumentoFisico fisico) {
        KitFisico kitAtual = kitFisicoInstrumentoRepository.findByInstrumentoFisico_IdAndAtivoTrue(fisico.getId())
                .map(KitFisicoInstrumento::getKitFisico)
                .orElse(null);
        KitVersion versaoAtual = kitAtual != null ? kitAtual.getKitVersaoAtual() : null;
        return new InstrumentoFisicoDto(fisico.getId(), fisico.getTenant().getId(), fisico.getInstrumento().getId(),
                fisico.getInstrumento().getNome(), fisico.getIdentificadorUnico(), fisico.getStatus(),
                fisico.getLocalizacao(), fisico.getObservacoes(), fisico.getAtivo(),
                kitAtual != null ? kitAtual.getId() : null,
                kitAtual != null ? kitAtual.getIdentificadorUnico() : null,
                kitAtual != null && kitAtual.getKit() != null ? kitAtual.getKit().getNome() : null,
                versaoAtual != null ? versaoAtual.getNumeroVersao() : null);
    }

    private KitFisicoDto toKitFisicoDto(KitFisico fisico) {
        KitVersion versao = fisico.getKitVersaoAtual();
        KitProcedimento kit = fisico.getKit();
        User aprovador = fisico.getAprovadoPor();
        List<String> pendencias = verificarPendenciasComposicao(fisico);
        return new KitFisicoDto(fisico.getId(), fisico.getTenant().getId(), kit != null ? kit.getId() : null,
                kit != null ? kit.getNome() : null, versao != null ? versao.getId() : null,
                versao != null ? versao.getNumeroVersao() : null,
                fisico.getIdentificadorUnico(), fisico.getStatus(), fisico.getLocalizacao(),
                fisico.getObservacoes(), fisico.getAtivo(),
                fisico.getStatusAprovacao() != null ? fisico.getStatusAprovacao() : StatusAprovacaoCme.PENDENTE,
                aprovador != null ? aprovador.getId() : null,
                aprovador != null ? aprovador.getFullName() : null,
                fisico.getAprovadoEm(),
                pendencias.isEmpty(),
                pendencias);
    }

    private KitFisicoInstrumentoDto toKitFisicoInstrumentoDto(KitFisicoInstrumento vinculo) {
        InstrumentoFisico fisico = vinculo.getInstrumentoFisico();
        Instrumento instrumento = fisico.getInstrumento();
        return new KitFisicoInstrumentoDto(vinculo.getId(), vinculo.getKitFisico().getId(), fisico.getId(),
                fisico.getIdentificadorUnico(), instrumento.getId(), instrumento.getNome(),
                vinculo.getVinculadoEm(), vinculo.getDesvinculadoEm(), vinculo.getObservacoes(), vinculo.getAtivo());
    }
}
