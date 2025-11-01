package com.erp.qualitascareapi.security.application;

import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.security.api.dto.RoleDto;
import com.erp.qualitascareapi.security.api.dto.RoleRequest;
import com.erp.qualitascareapi.security.domains.Role;
import com.erp.qualitascareapi.security.repo.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;

    public RoleService(RoleRepository roleRepository, TenantRepository tenantRepository) {
        this.roleRepository = roleRepository;
        this.tenantRepository = tenantRepository;
    }

    @Transactional(readOnly = true)
    public Page<RoleDto> list(Pageable pageable) {
        return roleRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public RoleDto get(Long id) {
        return roleRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
    }

    @Transactional
    public RoleDto create(RoleRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));

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

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));

        role.setName(request.name());
        role.setDescription(request.description());
        role.setTenant(tenant);
        return toDto(role);
    }

    @Transactional
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", id);
        }
        roleRepository.deleteById(id);
    }

    private RoleDto toDto(Role role) {
        Tenant tenant = role.getTenant();
        return new RoleDto(role.getId(), role.getName(), role.getDescription(),
                tenant != null ? tenant.getId() : null);
    }
}
