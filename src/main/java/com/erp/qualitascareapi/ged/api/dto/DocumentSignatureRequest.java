package com.erp.qualitascareapi.ged.api.dto;

import jakarta.validation.constraints.NotNull;

public record DocumentSignatureRequest(
        @NotNull Long signerId,
        String roleLabel
) {
}
