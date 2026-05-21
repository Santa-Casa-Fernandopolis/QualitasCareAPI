package com.erp.qualitascareapi.ged.api.dto;

import com.erp.qualitascareapi.ged.enums.DocumentStatus;
import com.erp.qualitascareapi.ged.enums.DocumentType;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record DocumentDashboardDto(
        OffsetDateTime generatedAt,
        int expiringWindowDays,
        long totalDocuments,
        long publishedDocuments,
        long inWorkflowDocuments,
        long expiredDocuments,
        long expiringSoonDocuments,
        long withCurrentVersion,
        long withoutCurrentVersion,
        long requiresTraining,
        long requiresLegalOpinion,
        long pendingSignatures,
        long pendingApprovalRequests,
        List<StatusCountDto> statusDistribution,
        List<TypeCountDto> typeDistribution,
        List<DepartmentCountDto> departmentDistribution,
        List<CriticalDocumentDto> criticalValidity
) {

    public record StatusCountDto(
            DocumentStatus status,
            long value
    ) {}

    public record TypeCountDto(
            DocumentType type,
            long value
    ) {}

    public record DepartmentCountDto(
            String department,
            long value
    ) {}

    public record CriticalDocumentDto(
            Long id,
            String codigo,
            String titulo,
            DocumentType tipo,
            DocumentStatus status,
            Long setorResponsavelId,
            String setorResponsavelNome,
            LocalDate dataVigenciaInicio,
            LocalDate dataVigenciaFim,
            Long versaoAtualId,
            String versaoAtual
    ) {}
}
