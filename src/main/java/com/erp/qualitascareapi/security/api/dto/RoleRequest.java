package com.erp.qualitascareapi.security.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoleRequest(
        @NotBlank String name,
        String description,
        @NotNull Long tenantId
) {
}
