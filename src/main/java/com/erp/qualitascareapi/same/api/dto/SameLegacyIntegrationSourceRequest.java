package com.erp.qualitascareapi.same.api.dto;

import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SameLegacyIntegrationSourceRequest(
        @NotNull Long tenantId,
        @NotBlank @Size(max = 120) String name,
        @NotNull SameSourceSystem sourceSystem,
        @NotBlank @Size(max = 500) String jdbcUrl,
        @Size(max = 120) String username,
        @Size(max = 500) String password,
        Boolean active
) {
}
