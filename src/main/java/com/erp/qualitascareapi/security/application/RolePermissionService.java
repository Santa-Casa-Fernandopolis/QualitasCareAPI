package com.erp.qualitascareapi.security.application;

import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.security.api.dto.RolePermissionDto;
import com.erp.qualitascareapi.security.api.dto.RolePermissionRequest;
import com.erp.qualitascareapi.security.domains.Permission;
import com.erp.qualitascareapi.security.domains.Role;
import com.erp.qualitascareapi.security.domains.RolePermission;
import com.erp.qualitascareapi.security.repo.PermissionRepository;
import com.erp.qualitascareapi.security.repo.RolePermissionRepository;
import com.erp.qualitascareapi.security.repo.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final TenantRepository tenantRepository;

    public RolePermissionService(RolePermissionRepository rolePermissionRepository,
                                 RoleRepository roleRepository,
                                 PermissionRepository permissionRepository,
                                 TenantRepository tenantRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.tenantRepository = tenantRepository;
    }

    @Transactional(readOnly = true)
    public Page<RolePermissionDto> list(Pageable pageable) {
        return rolePermissionRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public RolePermissionDto get(Long id) {
        return rolePermissionRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("RolePermission", id));
    }

    @Transactional
    public RolePermissionDto create(RolePermissionRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));
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

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));
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
        if (!rolePermissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("RolePermission", id);
        }
        rolePermissionRepository.deleteById(id);
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
}
