package com.erp.qualitascareapi.ged.api.dto;

import com.erp.qualitascareapi.approval.core.enums.ApprovalDecision;
import com.erp.qualitascareapi.approval.core.enums.ApprovalStepStatus;

import java.time.LocalDateTime;

public record ApprovalStepDto(
        Long id,
        Integer stageOrder,
        String stageCode,
        String requiredRole,
        Long scopeSetorId,
        String scopeSetorNome,
        ApprovalStepStatus status,
        ApprovalDecision decision,
        Long decidedById,
        String decidedByName,
        LocalDateTime decidedAt,
        String comment,
        String returnToStageCode
) {
}
