package com.erp.qualitascareapi.iam.application;

import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.iam.api.dto.TenantDto;
import com.erp.qualitascareapi.iam.api.dto.TenantLoginOptionDto;
import com.erp.qualitascareapi.iam.api.dto.TenantRequest;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    public TenantService(TenantRepository tenantRepository, UserRepository userRepository) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<TenantDto> list(Pageable pageable) {
        return tenantRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public TenantDto get(Long id) {
        return tenantRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", id));
    }

    @Transactional
    public TenantDto create(TenantRequest request) {
        Tenant tenant = new Tenant();
        applyRequest(request, tenant);
        return toDto(tenantRepository.save(tenant));
    }

    @Transactional
    public TenantDto update(Long id, TenantRequest request) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", id));
        applyRequest(request, tenant);
        return toDto(tenant);
    }

    @Transactional
    public void delete(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tenant", id);
        }
        tenantRepository.deleteById(id);
    }

    private void applyRequest(TenantRequest request, Tenant tenant) {
        tenant.setCode(request.code());
        tenant.setName(request.name());
        tenant.setCnpj(request.cnpj());
        tenant.setLogo(request.logo());
        tenant.setActive(request.active());
    }

    private TenantDto toDto(Tenant tenant) {
        return new TenantDto(
                tenant.getId(),
                tenant.getCode(),
                tenant.getName(),
                tenant.getCnpj(),
                tenant.getLogo(),
                tenant.isActive());
    }

    @Transactional(readOnly = true)
    public List<TenantLoginOptionDto> findAvailableTenantsForUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return List.of();
        }

        String normalized = username.trim();

        List<User> users = userRepository.findAllByUsernameIgnoreCase(normalized);

        Map<Long, Tenant> tenantsById = users.stream()
                .map(User::getTenant)
                .filter(Objects::nonNull)
                .filter(Tenant::isActive)
                .collect(Collectors.toMap(
                        Tenant::getId,
                        Function.identity(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));

        return tenantsById.values().stream()
                .sorted(Comparator.comparing(
                        Tenant::getName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(this::toLoginDto)
                .collect(Collectors.toList());
    }

    private TenantLoginOptionDto toLoginDto(Tenant tenant) {
        return new TenantLoginOptionDto(
                tenant.getId(),
                tenant.getCode(),
                tenant.getName(),
                tenant.getCnpj(),
                tenant.getLogo());
    }
}
