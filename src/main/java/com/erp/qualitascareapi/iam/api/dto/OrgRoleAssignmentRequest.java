package com.erp.qualitascareapi.iam.api.dto;

import com.erp.qualitascareapi.iam.enums.OrgRoleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record OrgRoleAssignmentRequest(
        @NotNull Long tenantId,
        @NotNull OrgRoleType roleType,
        @NotNull Long userId,
        Long setorId,
        @Valid PeriodoVigenciaRequest vigencia,
        Boolean active
) {
}
