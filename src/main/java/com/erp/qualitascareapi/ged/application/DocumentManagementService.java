package com.erp.qualitascareapi.ged.application;

import com.erp.qualitascareapi.approval.core.domain.ApprovalRequest;
import com.erp.qualitascareapi.approval.core.domain.ApprovalStep;
import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.approval.core.repo.ApprovalRequestRepository;
import com.erp.qualitascareapi.approval.core.repo.ApprovalStepRepository;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.ged.api.dto.*;
import com.erp.qualitascareapi.ged.domain.Document;
import com.erp.qualitascareapi.ged.domain.DocumentSignature;
import com.erp.qualitascareapi.ged.domain.DocumentVersion;
import com.erp.qualitascareapi.ged.enums.DocumentSignatureStatus;
import com.erp.qualitascareapi.ged.enums.DocumentStatus;
import com.erp.qualitascareapi.ged.repo.DocumentRepository;
import com.erp.qualitascareapi.ged.repo.DocumentSignatureRepository;
import com.erp.qualitascareapi.ged.repo.DocumentVersionRepository;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.SetorRepository;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.notificacao.application.NotificacaoService;
import com.erp.qualitascareapi.notificacao.enums.NivelNotificacao;
import com.erp.qualitascareapi.notificacao.enums.TipoNotificacao;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import com.erp.qualitascareapi.security.app.AuthContext;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DocumentManagementService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final DocumentSignatureRepository signatureRepository;
    private final TenantRepository tenantRepository;
    private final SetorRepository setorRepository;
    private final UserRepository userRepository;
    private final ApprovalRequestRepository approvalRequestRepository;
    private final ApprovalStepRepository approvalStepRepository;
    private final TenantScopeGuard tenantScopeGuard;
    private final DocumentFileStorageService fileStorageService;
    private final DocumentApprovalService approvalService;
    private final NotificacaoService notificacaoService;

    public DocumentManagementService(DocumentRepository documentRepository,
                                     DocumentVersionRepository versionRepository,
                                     DocumentSignatureRepository signatureRepository,
                                     TenantRepository tenantRepository,
                                     SetorRepository setorRepository,
                                     UserRepository userRepository,
                                     ApprovalRequestRepository approvalRequestRepository,
                                     ApprovalStepRepository approvalStepRepository,
                                     TenantScopeGuard tenantScopeGuard,
                                     DocumentFileStorageService fileStorageService,
                                     DocumentApprovalService approvalService,
                                     NotificacaoService notificacaoService) {
        this.documentRepository = documentRepository;
        this.versionRepository = versionRepository;
        this.signatureRepository = signatureRepository;
        this.tenantRepository = tenantRepository;
        this.setorRepository = setorRepository;
        this.userRepository = userRepository;
        this.approvalRequestRepository = approvalRequestRepository;
        this.approvalStepRepository = approvalStepRepository;
        this.tenantScopeGuard = tenantScopeGuard;
        this.fileStorageService = fileStorageService;
        this.approvalService = approvalService;
        this.notificacaoService = notificacaoService;
    }

    @Transactional(readOnly = true)
    public Page<DocumentDto> list(String search, DocumentStatus status, Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        Specification<Document> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (tenantId != null) {
                predicates.add(cb.equal(root.get("tenant").get("id"), tenantId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (StringUtils.hasText(search)) {
                String like = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("titulo")), like)
                ));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return documentRepository.findAll(spec, pageable).map(this::toDocumentDto);
    }

    @Transactional(readOnly = true)
    public Page<DocumentDto> library(String search, Pageable pageable) {
        Specification<Document> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Long tenantId = tenantScopeGuard.currentTenantId();
            if (tenantId != null) {
                predicates.add(cb.equal(root.get("tenant").get("id"), tenantId));
            }
            predicates.add(cb.equal(root.get("status"), DocumentStatus.PUBLICADO));
            if (StringUtils.hasText(search)) {
                String like = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("titulo")), like)
                ));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return documentRepository.findAll(spec, pageable).map(this::toDocumentDto);
    }

    @Transactional(readOnly = true)
    public DocumentDto get(Long id) {
        return toDocumentDto(loadDocument(id));
    }

    public DocumentDto create(DocumentRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", request.tenantId()));
        documentRepository.findByTenant_IdAndCodigoIgnoreCase(tenant.getId(), request.codigo())
                .ifPresent(existing -> {
                    throw new BadRequestException("Já existe documento com este código", Map.of("codigo", request.codigo()));
                });
        Document document = new Document();
        document.setTenant(tenant);
        document.setStatus(DocumentStatus.RASCUNHO);
        applyRequest(document, request);
        return toDocumentDto(documentRepository.save(document));
    }

    public DocumentDto update(Long id, DocumentRequest request) {
        Document document = loadDocument(id);
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        if (!document.getTenant().getId().equals(request.tenantId())) {
            throw new BadRequestException("Tenant do documento não pode ser alterado", Map.of("documentId", id));
        }
        applyRequest(document, request);
        return toDocumentDto(document);
    }

    public DocumentVersionDto createVersion(Long documentId, DocumentVersionRequest request) {
        Document document = loadDocument(documentId);
        DocumentVersion version = new DocumentVersion();
        version.setTenant(document.getTenant());
        version.setDocumento(document);
        version.setVersaoMajor(request.versaoMajor());
        version.setVersaoMinor(request.versaoMinor());
        version.setResumoMudancas(request.resumoMudancas());
        version.setDataVigenciaInicio(request.dataVigenciaInicio());
        version.setDataVigenciaFim(request.dataVigenciaFim());
        version.setStatus(DocumentStatus.EM_ELABORACAO);
        document.setStatus(DocumentStatus.EM_ELABORACAO);
        return toVersionDto(versionRepository.save(version));
    }

    @Transactional(readOnly = true)
    public List<DocumentVersionDto> listVersions(Long documentId) {
        Document document = loadDocument(documentId);
        return versionRepository.findAllByDocumento_IdAndTenant_IdOrderByVersaoMajorDescVersaoMinorDesc(document.getId(), document.getTenant().getId())
                .stream().map(this::toVersionDto).toList();
    }

    public DocumentVersionDto uploadOriginal(Long versionId, MultipartFile file) {
        DocumentVersion version = loadVersion(versionId);
        EvidenciaArquivo evidencia = fileStorageService.store(version.getTenant(), version.getDocumento().getId(), version.getId(), file, currentUser());
        version.setArquivoOriginal(evidencia);
        return toVersionDto(version);
    }

    public DocumentVersionDto uploadPublished(Long versionId, MultipartFile file) {
        DocumentVersion version = loadVersion(versionId);
        EvidenciaArquivo evidencia = fileStorageService.store(version.getTenant(), version.getDocumento().getId(), version.getId(), file, currentUser());
        version.setArquivoPublicado(evidencia);
        version.setPdfArquivo(evidencia);
        version.setPdfSha256(evidencia.getHashSha256());
        return toVersionDto(version);
    }

    public ApprovalRequestDto submitForApproval(Long versionId) {
        DocumentVersion version = loadVersion(versionId);
        if (version.getArquivoOriginal() == null && version.getPdfArquivo() == null && version.getArquivoPublicado() == null) {
            throw new BadRequestException("Inclua o arquivo do documento antes de submeter para aprovação", Map.of("versionId", versionId));
        }
        version.setStatus(DocumentStatus.EM_APROVACAO);
        version.setSubmetidoEm(LocalDateTime.now());
        version.getDocumento().setStatus(DocumentStatus.EM_APROVACAO);
        ApprovalRequest approvalRequest = approvalService.start(version, currentUser());
        return approvalService.toDto(approvalRequest);
    }

    public ApprovalRequestDto decide(Long stepId, ApprovalDecisionRequest request) {
        approvalService.decide(stepId, currentUser(), request.decision(), request.comment(), request.returnToStageCode());
        ApprovalStep step = approvalStepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Etapa de aprovação", stepId));
        return approvalService.toDto(step.getRequest());
    }

    public DocumentVersionDto publish(Long versionId, PublishDocumentRequest request) {
        DocumentVersion version = loadVersion(versionId);
        version.setDataVigenciaInicio(request.dataVigenciaInicio() != null ? request.dataVigenciaInicio() : version.getDataVigenciaInicio());
        version.setDataVigenciaFim(request.dataVigenciaFim() != null ? request.dataVigenciaFim() : version.getDataVigenciaFim());
        version.setObservacoesPublicacao(request.observacoesPublicacao());
        version.setStatus(DocumentStatus.PUBLICADO);
        version.setPublicadoEm(LocalDateTime.now());
        version.getDocumento().setStatus(DocumentStatus.PUBLICADO);
        version.getDocumento().setVersaoAtual(version);
        version.getDocumento().setDataVigenciaInicio(version.getDataVigenciaInicio());
        version.getDocumento().setDataVigenciaFim(version.getDataVigenciaFim());
        approvalRequestRepository.findFirstByTenant_IdAndDomainAndTargetKeyOrderByRequestedAtDesc(
                version.getTenant().getId(), ApprovalDomain.VERSAO_DOCUMENTO, version.getApprovalKey()
        ).ifPresent(approval -> approval.setStatus(com.erp.qualitascareapi.approval.core.enums.ApprovalRequestStatus.PUBLICADA));
        return toVersionDto(version);
    }

    @Transactional(readOnly = true)
    public ApprovalRequestDto getApproval(Long versionId) {
        DocumentVersion version = loadVersion(versionId);
        ApprovalRequest approval = approvalRequestRepository.findFirstByTenant_IdAndDomainAndTargetKeyOrderByRequestedAtDesc(
                        version.getTenant().getId(), ApprovalDomain.VERSAO_DOCUMENTO, version.getApprovalKey())
                .orElseThrow(() -> new ResourceNotFoundException("Fluxo de aprovação da versão", versionId));
        return approvalService.toDto(approval);
    }

    @Transactional(readOnly = true)
    public List<ApprovalDecisionDto> approvalHistory(Long approvalRequestId) {
        ApprovalRequest approval = approvalRequestRepository.findById(approvalRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação de aprovação", approvalRequestId));
        tenantScopeGuard.checkTenantAccess(approval.getTenant().getId());
        return approvalService.history(approvalRequestId);
    }

    public DocumentSignatureDto requestSignature(Long versionId, DocumentSignatureRequest request) {
        DocumentVersion version = loadVersion(versionId);
        User signer = loadTenantUser(version.getTenant().getId(), request.signerId());
        DocumentSignature signature = new DocumentSignature();
        signature.setTenant(version.getTenant());
        signature.setDocumentVersion(version);
        signature.setSigner(signer);
        signature.setRoleLabel(request.roleLabel());
        signature.setStatus(DocumentSignatureStatus.PENDENTE);
        DocumentSignature salva = signatureRepository.save(signature);

        // Notifica o signatário pessoalmente
        String docTitulo   = version.getDocumento().getTitulo();
        String roleLabel   = request.roleLabel() != null ? request.roleLabel() : "Signatário";
        String titulo      = "Assinatura solicitada: " + docTitulo;
        String mensagem    = String.format(
                "Você foi solicitado a assinar o documento '%s' na condição de '%s'. " +
                "Acesse o sistema para revisar e assinar.",
                docTitulo, roleLabel);
        notificacaoService.gerar(version.getTenant().getId(),
                TipoNotificacao.GED_ASSINATURA_SOLICITADA, NivelNotificacao.INFO,
                titulo, mensagem, salva.getId(), "ASSINATURA", signer.getId());

        return toSignatureDto(salva);
    }

    public DocumentSignatureDto sign(Long signatureId, String comment) {
        DocumentSignature signature = signatureRepository.findById(signatureId)
                .orElseThrow(() -> new ResourceNotFoundException("Assinatura", signatureId));
        tenantScopeGuard.checkTenantAccess(signature.getTenant().getId());
        signature.setStatus(DocumentSignatureStatus.ASSINADO);
        signature.setSignedAt(LocalDateTime.now());
        signature.setComment(comment);
        return toSignatureDto(signature);
    }

    @Transactional(readOnly = true)
    public List<DocumentSignatureDto> listSignatures(Long versionId) {
        DocumentVersion version = loadVersion(versionId);
        return signatureRepository.findAllByDocumentVersion_IdAndTenant_IdOrderByRequestedAtAsc(version.getId(), version.getTenant().getId())
                .stream().map(this::toSignatureDto).toList();
    }

    private void applyRequest(Document document, DocumentRequest request) {
        document.setCodigo(request.codigo());
        document.setTitulo(request.titulo());
        document.setTipo(request.tipo());
        document.setConfidencialidade(request.confidencialidade());
        document.setSetorResponsavel(request.setorResponsavelId() != null ? loadSetor(request.tenantId(), request.setorResponsavelId()) : null);
        document.setDataVigenciaInicio(request.dataVigenciaInicio());
        document.setDataVigenciaFim(request.dataVigenciaFim());
        document.setExigeTreinamento(Boolean.TRUE.equals(request.exigeTreinamento()));
        document.setNecessitaParecerJuridico(Boolean.TRUE.equals(request.necessitaParecerJuridico()));
        document.setPeriodicidadeRevisaoMeses(request.periodicidadeRevisaoMeses());
        document.setNivelONATarget(request.nivelONATarget());
        document.setRegulacoes(request.regulacoes());
        document.setObservacoesFluxo(request.observacoesFluxo());
    }

    private Document loadDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento", id));
        tenantScopeGuard.checkTenantAccess(document.getTenant().getId());
        return document;
    }

    private DocumentVersion loadVersion(Long id) {
        DocumentVersion version = versionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Versão do documento", id));
        tenantScopeGuard.checkTenantAccess(version.getTenant().getId());
        return version;
    }

    private Setor loadSetor(Long tenantId, Long setorId) {
        Setor setor = setorRepository.findById(setorId)
                .orElseThrow(() -> new ResourceNotFoundException("Setor", setorId));
        if (!setor.getTenant().getId().equals(tenantId)) {
            throw new BadRequestException("Setor não pertence ao tenant informado", Map.of("setorId", setorId, "tenantId", tenantId));
        }
        return setor;
    }

    private User currentUser() {
        AuthContext context = tenantScopeGuard.currentContext();
        if (context.userId() == null) {
            throw new BadRequestException("Usuário autenticado não identificado");
        }
        User user = userRepository.findById(context.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", context.userId()));
        tenantScopeGuard.checkTenantAccess(user.getTenant().getId());
        return user;
    }

    private User loadTenantUser(Long tenantId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", userId));
        if (!user.getTenant().getId().equals(tenantId)) {
            throw new BadRequestException("Usuário não pertence ao tenant do documento", Map.of("userId", userId, "tenantId", tenantId));
        }
        return user;
    }

    private DocumentDto toDocumentDto(Document document) {
        DocumentVersion version = document.getVersaoAtual();
        return new DocumentDto(
                document.getId(),
                document.getTenant().getId(),
                document.getTenant().getName(),
                document.getCodigo(),
                document.getTitulo(),
                document.getTipo(),
                document.getStatus(),
                document.getConfidencialidade(),
                document.getSetorResponsavel() != null ? document.getSetorResponsavel().getId() : null,
                document.getSetorResponsavel() != null ? document.getSetorResponsavel().getNome() : null,
                document.getDataVigenciaInicio(),
                document.getDataVigenciaFim(),
                version != null ? version.getId() : null,
                version != null ? version.getSemVer() : null,
                document.getExigeTreinamento(),
                document.getNecessitaParecerJuridico(),
                document.getPeriodicidadeRevisaoMeses(),
                document.getNivelONATarget(),
                document.getRegulacoes(),
                document.getObservacoesFluxo()
        );
    }

    private DocumentVersionDto toVersionDto(DocumentVersion version) {
        return new DocumentVersionDto(
                version.getId(),
                version.getDocumento().getId(),
                version.getDocumento().getCodigo(),
                version.getDocumento().getTitulo(),
                version.getVersaoMajor(),
                version.getVersaoMinor(),
                version.getSemVer(),
                version.getStatus(),
                version.getResumoMudancas(),
                version.getDataVigenciaInicio(),
                version.getDataVigenciaFim(),
                version.getArquivoOriginal() != null ? version.getArquivoOriginal().getId() : null,
                version.getArquivoPublicado() != null ? version.getArquivoPublicado().getId() : null,
                version.getPdfArquivo() != null ? version.getPdfArquivo().getId() : null,
                version.getPdfSha256(),
                version.getGeradoEm(),
                version.getSubmetidoEm(),
                version.getAprovadoEm(),
                version.getPublicadoEm()
        );
    }

    private DocumentSignatureDto toSignatureDto(DocumentSignature signature) {
        return new DocumentSignatureDto(
                signature.getId(),
                signature.getDocumentVersion().getId(),
                signature.getSigner().getId(),
                signature.getSigner().getFullName() != null ? signature.getSigner().getFullName() : signature.getSigner().getUsername(),
                signature.getRoleLabel(),
                signature.getStatus(),
                signature.getRequestedAt(),
                signature.getSignedAt(),
                signature.getComment()
        );
    }
}
