package com.erp.qualitascareapi.security.application;

import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.security.api.dto.RolePermissionDto;
import com.erp.qualitascareapi.security.api.dto.RolePermissionRequest;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import com.erp.qualitascareapi.security.domain.Permission;
import com.erp.qualitascareapi.security.domain.Role;
import com.erp.qualitascareapi.security.domain.RolePermission;
import com.erp.qualitascareapi.security.repo.PermissionRepository;
import com.erp.qualitascareapi.security.repo.RolePermissionRepository;
import com.erp.qualitascareapi.security.repo.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public RolePermissionService(RolePermissionRepository rolePermissionRepository,
                                 RoleRepository roleRepository,
                                 PermissionRepository permissionRepository,
                                 TenantRepository tenantRepository,
                                 TenantScopeGuard tenantScopeGuard) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    @Transactional(readOnly = true)
    public Page<RolePermissionDto> list(Pageable pageable) {
        Long tenantId = requireTenant();
        return rolePermissionRepository.findAllByTenant_Id(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public RolePermissionDto get(Long id) {
        RolePermission rolePermission = rolePermissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RolePermission", id));
        tenantScopeGuard.checkTenantAccess(rolePermission.getTenant() != null ? rolePermission.getTenant().getId() : null);
        return toDto(rolePermission);
    }

    @Transactional
    public RolePermissionDto create(RolePermissionRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));
        tenantScopeGuard.checkRequestedTenant(tenant.getId());
        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new BadRequestException("Role not found", Map.of("roleId", request.roleId())));
        Permission permission = permissionRepository.findById(request.permissionId())
                .orElseThrow(() -> new BadRequestException("Permission not found", Map.of("permissionId", request.permissionId())));

        validateTenant(tenant, role.getTenant(), permission.getTenant());

        RolePermission rolePermission = new RolePermission();
        rolePermission.setTenant(tenant);
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        return toDto(rolePermissionRepository.save(rolePermission));
    }

    @Transactional
    public RolePermissionDto update(Long id, RolePermissionRequest request) {
        RolePermission rolePermission = rolePermissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RolePermission", id));
        tenantScopeGuard.checkTenantAccess(rolePermission.getTenant() != null ? rolePermission.getTenant().getId() : null);

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));
        tenantScopeGuard.checkRequestedTenant(tenant.getId());
        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new BadRequestException("Role not found", Map.of("roleId", request.roleId())));
        Permission permission = permissionRepository.findById(request.permissionId())
                .orElseThrow(() -> new BadRequestException("Permission not found", Map.of("permissionId", request.permissionId())));

        validateTenant(tenant, role.getTenant(), permission.getTenant());

        rolePermission.setTenant(tenant);
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        return toDto(rolePermission);
    }

    @Transactional
    public void delete(Long id) {
        RolePermission rolePermission = rolePermissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RolePermission", id));
        tenantScopeGuard.checkTenantAccess(rolePermission.getTenant() != null ? rolePermission.getTenant().getId() : null);
        rolePermissionRepository.delete(rolePermission);
    }

    private void validateTenant(Tenant tenant, Tenant roleTenant, Tenant permissionTenant) {
        if (!roleTenant.getId().equals(tenant.getId()) || !permissionTenant.getId().equals(tenant.getId())) {
            throw new BadRequestException("Tenant mismatch for role or permission", Map.of("tenantId", tenant.getId()));
        }
    }

    private RolePermissionDto toDto(RolePermission rolePermission) {
        return new RolePermissionDto(rolePermission.getId(),
                rolePermission.getTenant() != null ? rolePermission.getTenant().getId() : null,
                rolePermission.getRole() != null ? rolePermission.getRole().getId() : null,
                rolePermission.getPermission() != null ? rolePermission.getPermission().getId() : null);
    }

    private Long requireTenant() {
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (tenantId == null) {
            throw new AccessDeniedException("Tenant context not available");
        }
        return tenantId;
    }
}
