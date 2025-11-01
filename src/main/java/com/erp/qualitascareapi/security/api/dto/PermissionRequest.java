package com.erp.qualitascareapi.security.api.dto;

import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import jakarta.validation.constraints.NotNull;

public record PermissionRequest(
        @NotNull ResourceType resource,
        @NotNull Action action,
        String feature,
        String code,
        @NotNull Long tenantId
) {
}
