package com.erp.qualitascareapi.security.api.dto;

import jakarta.validation.constraints.NotNull;

public record RolePermissionRequest(
        @NotNull Long tenantId,
        @NotNull Long roleId,
        @NotNull Long permissionId
) {
}
