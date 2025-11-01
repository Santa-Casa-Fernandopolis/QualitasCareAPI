package com.erp.qualitascareapi.iam.api;

import com.erp.qualitascareapi.iam.api.dto.TenantDto;
import com.erp.qualitascareapi.iam.api.dto.TenantRequest;
import com.erp.qualitascareapi.iam.application.TenantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping
    public Page<TenantDto> list(Pageable pageable) {
        return tenantService.list(pageable);
    }

    @GetMapping("/{id}")
    public TenantDto get(@PathVariable Long id) {
        return tenantService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TenantDto create(@Validated @RequestBody TenantRequest request) {
        return tenantService.create(request);
    }

    @PutMapping("/{id}")
    public TenantDto update(@PathVariable Long id, @Validated @RequestBody TenantRequest request) {
        return tenantService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        tenantService.delete(id);
    }
}
