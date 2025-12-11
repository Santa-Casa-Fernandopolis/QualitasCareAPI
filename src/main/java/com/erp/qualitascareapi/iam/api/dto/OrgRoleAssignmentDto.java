package com.erp.qualitascareapi.iam.api.dto;

import com.erp.qualitascareapi.iam.enums.OrgRoleType;
import com.erp.qualitascareapi.iam.enums.TipoSetor;

public record OrgRoleAssignmentDto(
        Long id,
        Long tenantId,
        String tenantCode,
        String tenantName,
        OrgRoleType roleType,
        Long userId,
        String username,
        String userFullName,
        Long setorId,
        String setorNome,
        TipoSetor setorTipo,
        PeriodoVigenciaDto vigencia,
        Boolean active
) {
}
