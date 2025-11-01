package com.erp.qualitascareapi.security.api.dto;

import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.Effect;
import com.erp.qualitascareapi.security.enums.ResourceType;

import java.time.LocalDateTime;

public record UserPermissionOverrideDto(
        Long id,
        Long tenantId,
        Long userId,
        ResourceType resource,
        Action action,
        String feature,
        Effect effect,
        int priority,
        String reason,
        LocalDateTime validFrom,
        LocalDateTime validUntil,
        boolean approved,
        boolean dualApprovalRequired,
        String requestedBy,
        String approvedBy,
        LocalDateTime approvedAt
) {
}
