package com.erp.qualitascareapi.iam.api.dto;

import jakarta.validation.constraints.NotBlank;

public record TenantRequest(
        @NotBlank String code,
        @NotBlank String name,
        boolean active
) {
}
