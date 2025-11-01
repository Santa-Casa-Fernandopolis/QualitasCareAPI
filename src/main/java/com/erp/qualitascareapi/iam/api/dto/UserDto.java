package com.erp.qualitascareapi.iam.api.dto;

import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.Set;

public record UserDto(
        Long id,
        String username,
        String fullName,
        String department,
        UserStatus status,
        IdentityOrigin origin,
        LocalDateTime createdAt,
        LocalDateTime activatedAt,
        LocalDateTime expiresAt,
        LocalDateTime updatedAt,
        Long tenantId,
        String tenantCode,
        Set<RoleSummaryDto> roles
) {
}
