package com.erp.qualitascareapi.iam.api.dto;

import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

public record UserCreateRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 6) String password,
        String fullName,
        String department,
        @NotNull Long tenantId,
        UserStatus status,
        IdentityOrigin origin,
        LocalDateTime activatedAt,
        LocalDateTime expiresAt,
        Set<Long> roleIds
) {
}
