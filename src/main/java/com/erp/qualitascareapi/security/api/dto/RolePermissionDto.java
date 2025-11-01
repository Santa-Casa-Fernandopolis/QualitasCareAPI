package com.erp.qualitascareapi.security.api.dto;

public record RolePermissionDto(Long id, Long tenantId, Long roleId, Long permissionId) {
}
