package com.erp.qualitascareapi.same.api.dto;

import com.erp.qualitascareapi.same.enums.SameSourceSystem;

import java.time.LocalDateTime;

public record SameLegacyIntegrationSourceDto(
        Long id,
        Long tenantId,
        String name,
        SameSourceSystem sourceSystem,
        String jdbcUrl,
        String username,
        boolean passwordConfigured,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
