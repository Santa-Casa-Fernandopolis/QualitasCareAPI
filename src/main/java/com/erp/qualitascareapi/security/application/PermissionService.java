package com.erp.qualitascareapi.security.application;

import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.security.api.dto.PermissionDto;
import com.erp.qualitascareapi.security.api.dto.PermissionRequest;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import com.erp.qualitascareapi.security.domain.Permission;
import com.erp.qualitascareapi.security.repo.PermissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public PermissionService(PermissionRepository permissionRepository,
                             TenantRepository tenantRepository,
                             TenantScopeGuard tenantScopeGuard) {
        this.permissionRepository = permissionRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    @Transactional(readOnly = true)
    public Page<PermissionDto> list(Pageable pageable) {
        Long tenantId = requireTenant();
        return permissionRepository.findAllByTenant_Id(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public PermissionDto get(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", id));
        tenantScopeGuard.checkTenantAccess(permission.getTenant() != null ? permission.getTenant().getId() : null);
        return toDto(permission);
    }

    @Transactional
    public PermissionDto create(PermissionRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));
        tenantScopeGuard.checkRequestedTenant(tenant.getId());

        Permission permission = new Permission();
        permission.setTenant(tenant);
        permission.setResource(request.resource());
        permission.setAction(request.action());
        permission.setFeature(request.feature());
        permission.setCode(request.code());
        return toDto(permissionRepository.save(permission));
    }

    @Transactional
    public PermissionDto update(Long id, PermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", id));
        tenantScopeGuard.checkTenantAccess(permission.getTenant() != null ? permission.getTenant().getId() : null);

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));
        tenantScopeGuard.checkRequestedTenant(tenant.getId());

        permission.setTenant(tenant);
        permission.setResource(request.resource());
        permission.setAction(request.action());
        permission.setFeature(request.feature());
        permission.setCode(request.code());
        return toDto(permission);
    }

    @Transactional
    public void delete(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", id));
        tenantScopeGuard.checkTenantAccess(permission.getTenant() != null ? permission.getTenant().getId() : null);
        permissionRepository.delete(permission);
    }

    private PermissionDto toDto(Permission permission) {
        Tenant tenant = permission.getTenant();
        return new PermissionDto(permission.getId(), permission.getResource(), permission.getAction(),
                permission.getFeature(), permission.getCode(), tenant != null ? tenant.getId() : null);
    }

    private Long requireTenant() {
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (tenantId == null) {
            throw new AccessDeniedException("Tenant context not available");
        }
        return tenantId;
    }
}
