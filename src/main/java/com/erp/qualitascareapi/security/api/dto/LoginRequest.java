package com.erp.qualitascareapi.security.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "username is required") @Size(max = 255, message = "username must not exceed 255 characters") String username,
        @NotBlank(message = "password is required") String password,
        @Size(max = 255, message = "tenantCode must not exceed 255 characters") String tenantCode
) {
}
