package com.erp.qualitascareapi.iam.api;

import com.erp.qualitascareapi.iam.api.dto.TenantDto;
import com.erp.qualitascareapi.iam.api.dto.TenantRequest;
import com.erp.qualitascareapi.iam.application.TenantService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @RequiresPermission(resource = ResourceType.IAM_TENANT, action = Action.READ)
    @GetMapping
    public Page<TenantDto> list(Pageable pageable) {
        return tenantService.list(pageable);
    }

    @RequiresPermission(resource = ResourceType.IAM_TENANT, action = Action.READ)
    @GetMapping("/{id}")
    public TenantDto get(@PathVariable Long id) {
        return tenantService.get(id);
    }

    @RequiresPermission(resource = ResourceType.IAM_TENANT, action = Action.CREATE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TenantDto create(@Validated @RequestBody TenantRequest request) {
        return tenantService.create(request);
    }

    @RequiresPermission(resource = ResourceType.IAM_TENANT, action = Action.UPDATE)
    @PutMapping("/{id}")
    public TenantDto update(@PathVariable Long id, @Validated @RequestBody TenantRequest request) {
        return tenantService.update(id, request);
    }

    @RequiresPermission(resource = ResourceType.IAM_TENANT, action = Action.DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        tenantService.delete(id);
    }
}
