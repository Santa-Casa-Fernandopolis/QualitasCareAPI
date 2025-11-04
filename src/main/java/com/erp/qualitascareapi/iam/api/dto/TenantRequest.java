package com.erp.qualitascareapi.iam.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TenantRequest(
        @NotNull Long code,
        @NotBlank String name,
        @NotBlank @Size(max = 14) String cnpj,
        @Size(max = 255) String logo,
        boolean active
) {
}
