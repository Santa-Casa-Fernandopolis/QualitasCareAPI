package com.erp.qualitascareapi.iam.application;

import com.erp.qualitascareapi.iam.api.dto.TenantDto;
import com.erp.qualitascareapi.iam.api.dto.TenantRequest;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Transactional(readOnly = true)
    public Page<TenantDto> list(Pageable pageable) {
        return tenantRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public TenantDto get(Long id) {
        return tenantRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
        applyRequest(request, tenant);
        return toDto(tenant);
    }

    @Transactional
    public void delete(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found");
        }
        tenantRepository.deleteById(id);
    }

    private void applyRequest(TenantRequest request, Tenant tenant) {
        tenant.setCode(request.code());
        tenant.setName(request.name());
        tenant.setActive(request.active());
    }

    private TenantDto toDto(Tenant tenant) {
        return new TenantDto(tenant.getId(), tenant.getCode(), tenant.getName(), tenant.isActive());
    }
}
