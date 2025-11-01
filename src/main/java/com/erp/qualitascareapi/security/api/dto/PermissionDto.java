package com.erp.qualitascareapi.security.api.dto;

import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;

public record PermissionDto(
        Long id,
        ResourceType resource,
        Action action,
        String feature,
        String code,
        Long tenantId
) {
}
