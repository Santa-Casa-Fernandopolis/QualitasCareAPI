package com.erp.qualitascareapi.security.application;

import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.security.api.dto.PermissionDto;
import com.erp.qualitascareapi.security.api.dto.PermissionRequest;
import com.erp.qualitascareapi.security.domain.Permission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import com.erp.qualitascareapi.security.repo.PermissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public Page<PermissionDto> list(ResourceType resource,
                                    Action action,
                                    String feature,
                                    String code,
                                    Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        String normalizedFeature = emptyToNull(feature);
        String normalizedCode = emptyToNull(code);

        Specification<Permission> spec = Specification.where(null);

        if (tenantId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tenant").get("id"), tenantId));
        }
        if (resource != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("resource"), resource));
        }
        if (action != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("action"), action));
        }
        if (normalizedFeature != null) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("feature")), "%" + normalizedFeature.toLowerCase() + "%"));
        }
        if (normalizedCode != null) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("code")), "%" + normalizedCode.toLowerCase() + "%"));
        }

        return permissionRepository.findAll(spec, pageable).map(this::toDto);
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

    private String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
