package com.erp.qualitascareapi.iam.api.dto;

import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

public record UserUpdateRequest(
        String fullName,
        String department,
        UserStatus status,
        IdentityOrigin origin,
        LocalDateTime activatedAt,
        LocalDateTime expiresAt,
        @Size(min = 6) String password,
        Set<Long> roleIds
) {
}
