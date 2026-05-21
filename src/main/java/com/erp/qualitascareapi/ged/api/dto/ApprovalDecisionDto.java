package com.erp.qualitascareapi.ged.api.dto;

import com.erp.qualitascareapi.approval.core.enums.ApprovalDecision;
import com.erp.qualitascareapi.approval.core.enums.ApprovalStepStatus;

import java.time.LocalDateTime;

public record ApprovalDecisionDto(
        Long id,
        String fromStageCode,
        String toStageCode,
        ApprovalDecision decision,
        ApprovalStepStatus previousStatus,
        ApprovalStepStatus newStatus,
        Long decidedById,
        String decidedByName,
        LocalDateTime decidedAt,
        String comment
) {
}
