package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.domain.InstrumentoFisico;
import com.erp.qualitascareapi.cme.domain.KitFisico;
import com.erp.qualitascareapi.cme.domain.KitFisicoInstrumento;
import com.erp.qualitascareapi.cme.enums.IdentificacaoFisicaStatus;
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
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
                      NotificacaoService notificacaoService) {
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
        return new KitProcedimentoDto(saved.getId(), tenant.getId(), saved.getNome(), saved.getCodigo(),
                saved.getObservacoes(), saved.getAtivo());
    }

    public KitProcedimentoDto findKitById(Long id) {
        KitProcedimento k = kitProcedimentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kit não encontrado"));
        return new KitProcedimentoDto(k.getId(), k.getTenant().getId(), k.getNome(), k.getCodigo(),
                k.getObservacoes(), k.getAtivo());
    }

    public KitProcedimentoDto updateKit(Long id, KitProcedimentoRequest request) {
        KitProcedimento kit = kitProcedimentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kit não encontrado"));
        kit.setNome(request.nome());
        kit.setCodigo(request.codigo());
        kit.setObservacoes(request.observacoes());
        if (request.ativo() != null) kit.setAtivo(request.ativo());
        KitProcedimento saved = kitProcedimentoRepository.save(kit);
        return new KitProcedimentoDto(saved.getId(), saved.getTenant().getId(), saved.getNome(), saved.getCodigo(),
                saved.getObservacoes(), saved.getAtivo());
    }

    public Page<KitProcedimentoDto> listKits(Pageable pageable) {
        return kitProcedimentoRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(k -> new KitProcedimentoDto(k.getId(), k.getTenant().getId(), k.getNome(), k.getCodigo(),
                        k.getObservacoes(), k.getAtivo()));
    }

    public KitVersionDto createKitVersion(KitVersionRequest request) {
        KitProcedimento kit = kitProcedimentoRepository.findById(request.kitId())
                .orElseThrow(() -> new EntityNotFoundException("Kit não encontrado"));
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

    private KitVersionDto toKitVersionDto(KitVersion v) {
        User aprovador = v.getAprovadoPor();
        return new KitVersionDto(v.getId(), v.getKit().getId(), v.getNumeroVersao(),
                v.getVigenciaInicio(), v.getValidadeDias(), v.getAtivo(), v.getObservacoes(),
                v.getStatusAprovacao(), aprovador != null ? aprovador.getId() : null,
                aprovador != null ? aprovador.getFullName() : null, v.getAprovadoEm(), v.getRevalidadoEm());
    }

    private void notificarAprovacaoVersao(KitVersion version) {
        Long tenantId = version.getKit().getTenant().getId();
        Long versionId = version.getId();
        Integer numeroVersao = version.getNumeroVersao();
        String kitNome = version.getKit().getNome();

        Runnable notificationTask = () -> {
            try {
                notificacaoService.gerar(
                        tenantId,
                        TipoNotificacao.CME_KIT_VERSAO_APROVACAO_SOLICITADA,
                        NivelNotificacao.INFO,
                        "Versão de kit pendente de aprovação",
                        "A versão v" + numeroVersao + " do kit " + kitNome
                                + " foi cadastrada e precisa ser aprovada pela supervisora da CME.",
                        versionId,
                        "CME_KIT_VERSION"
                );
            } catch (RuntimeException ex) {
                log.warn("Não foi possível gerar notificação de aprovação da versão de kit {}.", versionId, ex);
            }
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    notificationTask.run();
                }
            });
        } else {
            notificationTask.run();
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
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        if (kitFisicoRepository.existsByTenant_IdAndIdentificadorUnicoIgnoreCase(request.tenantId(), request.identificadorUnico().trim())) {
            throw new ApplicationException(HttpStatus.CONFLICT, "kit-fisico.identificador-duplicado",
                    "Já existe um kit físico com este identificador.");
        }
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        KitProcedimento kit = kitProcedimentoRepository.findById(request.kitId())
                .orElseThrow(() -> new EntityNotFoundException("Kit não encontrado"));
        tenantScopeGuard.checkTenantAccess(kit.getTenant().getId());
        KitVersion versaoAtual = null;
        if (request.kitVersaoAtualId() != null) {
            versaoAtual = kitVersionRepository.findById(request.kitVersaoAtualId())
                    .orElseThrow(() -> new EntityNotFoundException("Versão de kit não encontrada"));
            if (!versaoAtual.getKit().getId().equals(kit.getId())) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "kit-fisico.versao-invalida",
                        "A versão informada não pertence ao kit selecionado.");
            }
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
        return toKitFisicoDto(kitFisicoRepository.save(fisico));
    }

    public Page<KitFisicoDto> listKitsFisicos(Pageable pageable) {
        return kitFisicoRepository.findAllByTenant_Id(tenantScopeGuard.currentTenantId(), pageable)
                .map(this::toKitFisicoDto);
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
        KitProcedimento kit = kitProcedimentoRepository.findById(request.kitId())
                .orElseThrow(() -> new EntityNotFoundException("Kit não encontrado"));
        tenantScopeGuard.checkTenantAccess(kit.getTenant().getId());
        KitVersion versaoAtual = null;
        if (request.kitVersaoAtualId() != null) {
            versaoAtual = kitVersionRepository.findById(request.kitVersaoAtualId())
                    .orElseThrow(() -> new EntityNotFoundException("Versão de kit não encontrada"));
            if (!versaoAtual.getKit().getId().equals(kit.getId())) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "kit-fisico.versao-invalida",
                        "A versão informada não pertence ao kit selecionado.");
            }
        }

        fisico.setKit(kit);
        fisico.setKitVersaoAtual(versaoAtual);
        fisico.setIdentificadorUnico(request.identificadorUnico().trim());
        fisico.setStatus(request.status() != null ? request.status() : IdentificacaoFisicaStatus.ATIVO);
        fisico.setLocalizacao(request.localizacao());
        fisico.setObservacoes(request.observacoes());
        fisico.setAtivo(request.ativo() != null ? request.ativo() : Boolean.TRUE);
        return toKitFisicoDto(kitFisicoRepository.save(fisico));
    }

    public KitFisicoInstrumentoDto vincularInstrumentoFisico(KitFisicoInstrumentoRequest request) {
        KitFisico kitFisico = kitFisicoRepository.findById(request.kitFisicoId())
                .orElseThrow(() -> new EntityNotFoundException("Kit físico não encontrado"));
        InstrumentoFisico instrumentoFisico = instrumentoFisicoRepository.findById(request.instrumentoFisicoId())
                .orElseThrow(() -> new EntityNotFoundException("Instrumento físico não encontrado"));
        tenantScopeGuard.checkTenantAccess(kitFisico.getTenant().getId());
        tenantScopeGuard.checkTenantAccess(instrumentoFisico.getTenant().getId());

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
        return toKitFisicoInstrumentoDto(kitFisicoInstrumentoRepository.save(vinculo));
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
        vinculo.setAtivo(Boolean.FALSE);
        vinculo.setDesvinculadoEm(LocalDate.now());
        kitFisicoInstrumentoRepository.save(vinculo);
    }

    private InstrumentoFisicoDto toInstrumentoFisicoDto(InstrumentoFisico fisico) {
        return new InstrumentoFisicoDto(fisico.getId(), fisico.getTenant().getId(), fisico.getInstrumento().getId(),
                fisico.getInstrumento().getNome(), fisico.getIdentificadorUnico(), fisico.getStatus(),
                fisico.getLocalizacao(), fisico.getObservacoes(), fisico.getAtivo());
    }

    private KitFisicoDto toKitFisicoDto(KitFisico fisico) {
        KitVersion versao = fisico.getKitVersaoAtual();
        return new KitFisicoDto(fisico.getId(), fisico.getTenant().getId(), fisico.getKit().getId(),
                fisico.getKit().getNome(), versao != null ? versao.getId() : null,
                versao != null ? versao.getNumeroVersao() : null,
                fisico.getIdentificadorUnico(), fisico.getStatus(), fisico.getLocalizacao(),
                fisico.getObservacoes(), fisico.getAtivo());
    }

    private KitFisicoInstrumentoDto toKitFisicoInstrumentoDto(KitFisicoInstrumento vinculo) {
        InstrumentoFisico fisico = vinculo.getInstrumentoFisico();
        Instrumento instrumento = fisico.getInstrumento();
        return new KitFisicoInstrumentoDto(vinculo.getId(), vinculo.getKitFisico().getId(), fisico.getId(),
                fisico.getIdentificadorUnico(), instrumento.getId(), instrumento.getNome(),
                vinculo.getVinculadoEm(), vinculo.getDesvinculadoEm(), vinculo.getObservacoes(), vinculo.getAtivo());
    }
}
