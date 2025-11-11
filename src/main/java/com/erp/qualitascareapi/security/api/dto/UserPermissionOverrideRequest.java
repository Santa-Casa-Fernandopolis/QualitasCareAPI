package com.erp.qualitascareapi.security.api.dto;

import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.Effect;
import com.erp.qualitascareapi.security.enums.ResourceType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UserPermissionOverrideRequest(
        @NotNull Long tenantId,
        @NotNull Long userId,
        @NotNull ResourceType resource,
        @NotNull Action action,
        String feature,
        @NotNull Effect effect,
        Integer priority,
        String reason,
        LocalDateTime validFrom,
        LocalDateTime validUntil,
        Long targetSetorId,
        Long requestedByUserId,
        LocalDateTime requestedAt,
        Long approvedByUserId,
        LocalDateTime approvedAt
) {
}
