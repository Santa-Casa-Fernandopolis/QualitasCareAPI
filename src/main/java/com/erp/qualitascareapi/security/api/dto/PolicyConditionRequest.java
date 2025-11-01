package com.erp.qualitascareapi.security.api.dto;

import jakarta.validation.constraints.NotBlank;

public record PolicyConditionRequest(
        @NotBlank String type,
        @NotBlank String operator,
        @NotBlank String value
) {
}
