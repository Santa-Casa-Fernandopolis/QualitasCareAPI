package com.erp.qualitascareapi.ged.api.dto;

import com.erp.qualitascareapi.approval.core.enums.ApprovalRequestStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ApprovalRequestDto(
        Long id,
        Long tenantId,
        String domain,
        String targetKey,
        ApprovalRequestStatus status,
        String flowNameSnapshot,
        Long requestedById,
        String requestedByName,
        LocalDateTime requestedAt,
        List<ApprovalStepDto> steps
) {
}
