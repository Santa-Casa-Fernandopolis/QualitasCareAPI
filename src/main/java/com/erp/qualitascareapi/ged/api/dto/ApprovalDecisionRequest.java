package com.erp.qualitascareapi.ged.api.dto;

import com.erp.qualitascareapi.approval.core.enums.ApprovalDecision;
import jakarta.validation.constraints.NotNull;

public record ApprovalDecisionRequest(
        @NotNull ApprovalDecision decision,
        String comment,
        String returnToStageCode
) {
}
