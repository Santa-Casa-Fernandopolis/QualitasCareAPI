package com.erp.qualitascareapi.ged.application;

import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.approval.core.enums.ApprovalRequestStatus;
import com.erp.qualitascareapi.approval.core.repo.ApprovalRequestRepository;
import com.erp.qualitascareapi.ged.api.dto.DocumentDashboardDto;
import com.erp.qualitascareapi.ged.domain.Document;
import com.erp.qualitascareapi.ged.domain.DocumentVersion;
import com.erp.qualitascareapi.ged.enums.DocumentSignatureStatus;
import com.erp.qualitascareapi.ged.enums.DocumentStatus;
import com.erp.qualitascareapi.ged.enums.DocumentType;
import com.erp.qualitascareapi.ged.repo.DocumentRepository;
import com.erp.qualitascareapi.ged.repo.DocumentSignatureRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;

@Service
public class DocumentDashboardService {

    public static final int EXPIRING_WINDOW_DAYS = 60;
    private static final int MAX_DEPARTMENTS = 8;
    private static final int MAX_CRITICAL_DOCUMENTS = 6;
    private static final List<DocumentStatus> WORKFLOW_STATUSES = List.of(
            DocumentStatus.EM_APROVACAO,
            DocumentStatus.EM_AJUSTE,
            DocumentStatus.AGUARDANDO_ASSINATURAS
    );
    private static final List<ApprovalRequestStatus> PENDING_APPROVAL_STATUSES = List.of(
            ApprovalRequestStatus.ABERTA,
            ApprovalRequestStatus.EM_ANDAMENTO,
            ApprovalRequestStatus.EM_AJUSTE,
            ApprovalRequestStatus.AGUARDANDO_ASSINATURAS
    );

    private final DocumentRepository documentRepository;
    private final DocumentSignatureRepository signatureRepository;
    private final ApprovalRequestRepository approvalRequestRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public DocumentDashboardService(DocumentRepository documentRepository,
                                    DocumentSignatureRepository signatureRepository,
                                    ApprovalRequestRepository approvalRequestRepository,
                                    TenantScopeGuard tenantScopeGuard) {
        this.documentRepository = documentRepository;
        this.signatureRepository = signatureRepository;
        this.approvalRequestRepository = approvalRequestRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    @Transactional(readOnly = true)
    public DocumentDashboardDto getDashboard() {
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (tenantId == null) {
            throw new AccessDeniedException("Tenant context is required");
        }

        LocalDate today = LocalDate.now();
        LocalDate expiringLimit = today.plusDays(EXPIRING_WINDOW_DAYS);
        long totalDocuments = documentRepository.countByTenant_Id(tenantId);
        long publishedDocuments = documentRepository.countByTenant_IdAndStatus(tenantId, DocumentStatus.PUBLICADO);
        long expiredDocuments = documentRepository.countByTenant_IdAndDataVigenciaFimBefore(tenantId, today);
        long expiringSoonDocuments = documentRepository.countByTenant_IdAndDataVigenciaFimBetween(tenantId, today, expiringLimit);
        long withCurrentVersion = documentRepository.countByTenant_IdAndVersaoAtualIsNotNull(tenantId);
        long withoutCurrentVersion = documentRepository.countByTenant_IdAndVersaoAtualIsNull(tenantId);
        long requiresTraining = documentRepository.countByTenant_IdAndExigeTreinamentoTrue(tenantId);
        long requiresLegalOpinion = documentRepository.countByTenant_IdAndNecessitaParecerJuridicoTrue(tenantId);
        long pendingSignatures = signatureRepository.countByTenant_IdAndStatus(tenantId, DocumentSignatureStatus.PENDENTE);
        long pendingApprovalRequests = PENDING_APPROVAL_STATUSES.stream()
                .mapToLong(status -> approvalRequestRepository.countByTenant_IdAndDomainAndStatus(
                        tenantId, ApprovalDomain.VERSAO_DOCUMENTO, status))
                .sum();
        long inWorkflowDocuments = WORKFLOW_STATUSES.stream()
                .mapToLong(status -> documentRepository.countByTenant_IdAndStatus(tenantId, status))
                .sum();

        return new DocumentDashboardDto(
                OffsetDateTime.now(),
                EXPIRING_WINDOW_DAYS,
                totalDocuments,
                publishedDocuments,
                inWorkflowDocuments,
                expiredDocuments,
                expiringSoonDocuments,
                withCurrentVersion,
                withoutCurrentVersion,
                requiresTraining,
                requiresLegalOpinion,
                pendingSignatures,
                pendingApprovalRequests,
                statusDistribution(tenantId),
                typeDistribution(tenantId),
                departmentDistribution(tenantId),
                criticalValidity(tenantId, expiringLimit)
        );
    }

    private List<DocumentDashboardDto.StatusCountDto> statusDistribution(Long tenantId) {
        EnumMap<DocumentStatus, Long> counts = new EnumMap<>(DocumentStatus.class);
        Arrays.stream(DocumentStatus.values()).forEach(status -> counts.put(status, 0L));
        documentRepository.countByStatus(tenantId).forEach(row ->
                counts.put((DocumentStatus) row[0], (Long) row[1])
        );

        return Arrays.stream(DocumentStatus.values())
                .map(status -> new DocumentDashboardDto.StatusCountDto(status, counts.getOrDefault(status, 0L)))
                .toList();
    }

    private List<DocumentDashboardDto.TypeCountDto> typeDistribution(Long tenantId) {
        EnumMap<DocumentType, Long> counts = new EnumMap<>(DocumentType.class);
        Arrays.stream(DocumentType.values()).forEach(type -> counts.put(type, 0L));
        documentRepository.countByType(tenantId).forEach(row ->
                counts.put((DocumentType) row[0], (Long) row[1])
        );

        return Arrays.stream(DocumentType.values())
                .map(type -> new DocumentDashboardDto.TypeCountDto(type, counts.getOrDefault(type, 0L)))
                .filter(item -> item.value() > 0)
                .sorted(Comparator.comparingLong(DocumentDashboardDto.TypeCountDto::value).reversed())
                .toList();
    }

    private List<DocumentDashboardDto.DepartmentCountDto> departmentDistribution(Long tenantId) {
        return documentRepository.countByDepartment(tenantId, PageRequest.of(0, MAX_DEPARTMENTS))
                .stream()
                .map(row -> new DocumentDashboardDto.DepartmentCountDto((String) row[0], (Long) row[1]))
                .toList();
    }

    private List<DocumentDashboardDto.CriticalDocumentDto> criticalValidity(Long tenantId, LocalDate expiringLimit) {
        return documentRepository.findCriticalValidity(tenantId, expiringLimit, PageRequest.of(0, MAX_CRITICAL_DOCUMENTS))
                .stream()
                .map(this::toCriticalDocumentDto)
                .toList();
    }

    private DocumentDashboardDto.CriticalDocumentDto toCriticalDocumentDto(Document document) {
        DocumentVersion version = document.getVersaoAtual();
        return new DocumentDashboardDto.CriticalDocumentDto(
                document.getId(),
                document.getCodigo(),
                document.getTitulo(),
                document.getTipo(),
                document.getStatus(),
                document.getSetorResponsavel() != null ? document.getSetorResponsavel().getId() : null,
                document.getSetorResponsavel() != null ? document.getSetorResponsavel().getNome() : null,
                document.getDataVigenciaInicio(),
                document.getDataVigenciaFim(),
                version != null ? version.getId() : null,
                version != null ? version.getSemVer() : null
        );
    }
}
