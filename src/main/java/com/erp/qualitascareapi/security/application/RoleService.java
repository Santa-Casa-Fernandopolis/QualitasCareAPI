package com.erp.qualitascareapi.security.application;

import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.security.api.dto.RoleDto;
import com.erp.qualitascareapi.security.api.dto.RoleRequest;
import com.erp.qualitascareapi.security.domain.Role;
import com.erp.qualitascareapi.security.repo.RoleRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public RoleService(RoleRepository roleRepository,
                       TenantRepository tenantRepository,
                       TenantScopeGuard tenantScopeGuard) {
        this.roleRepository = roleRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    @Transactional(readOnly = true)
    public Page<RoleDto> list(Pageable pageable) {
        Long tenantId = requireTenant();
        return roleRepository.findAllByTenant_Id(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public RoleDto get(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        tenantScopeGuard.checkTenantAccess(role.getTenant() != null ? role.getTenant().getId() : null);
        return toDto(role);
    }

    @Transactional
    public RoleDto create(RoleRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));
        tenantScopeGuard.checkRequestedTenant(tenant.getId());

        Role role = new Role();
        role.setName(request.name());
        role.setDescription(request.description());
        role.setTenant(tenant);
        return toDto(roleRepository.save(role));
    }

    @Transactional
    public RoleDto update(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        tenantScopeGuard.checkTenantAccess(role.getTenant() != null ? role.getTenant().getId() : null);

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));
        tenantScopeGuard.checkRequestedTenant(tenant.getId());

        role.setName(request.name());
        role.setDescription(request.description());
        role.setTenant(tenant);
        return toDto(role);
    }

    @Transactional
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        tenantScopeGuard.checkTenantAccess(role.getTenant() != null ? role.getTenant().getId() : null);
        roleRepository.delete(role);
    }

    private Long requireTenant() {
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (tenantId == null) {
            throw new AccessDeniedException("Tenant context not available");
        }
        return tenantId;
    }

    private RoleDto toDto(Role role) {
        Tenant tenant = role.getTenant();
        return new RoleDto(role.getId(), role.getName(), role.getDescription(),
                tenant != null ? tenant.getId() : null);
    }
}
