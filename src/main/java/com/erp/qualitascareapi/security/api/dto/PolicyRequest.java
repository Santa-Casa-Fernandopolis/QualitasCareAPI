package com.erp.qualitascareapi.security.api.dto;

import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.Effect;
import com.erp.qualitascareapi.security.enums.ResourceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

public record PolicyRequest(
        @NotNull Long tenantId,
        @NotNull ResourceType resource,
        @NotNull Action action,
        String feature,
        @NotNull Effect effect,
        Boolean enabled,
        Integer priority,
        String description,
        Set<Long> roleIds,
        @Valid List<PolicyConditionRequest> conditions
) {
}
