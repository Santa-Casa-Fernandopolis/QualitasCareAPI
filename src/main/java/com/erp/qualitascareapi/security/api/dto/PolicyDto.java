package com.erp.qualitascareapi.security.api.dto;

import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.Effect;
import com.erp.qualitascareapi.security.enums.ResourceType;

import java.util.List;
import java.util.Set;

public record PolicyDto(
        Long id,
        Long tenantId,
        ResourceType resource,
        Action action,
        String feature,
        Effect effect,
        boolean enabled,
        int priority,
        String description,
        Set<PolicyRoleDto> roles,
        List<PolicyConditionDto> conditions
) {
}
