package com.erp.qualitascareapi.security.api.dto;

import java.util.List;
import java.util.Set;

public record CurrentUserPermissionsResponse(
        Long userId,
        String username,
        Long tenantId,
        String department,
        Set<String> roles,
        List<String> permissions
) {
}
