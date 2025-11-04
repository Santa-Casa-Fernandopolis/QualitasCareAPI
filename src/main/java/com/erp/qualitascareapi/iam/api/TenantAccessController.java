package com.erp.qualitascareapi.iam.api;

import com.erp.qualitascareapi.iam.api.dto.TenantLoginOptionDto;
import com.erp.qualitascareapi.iam.application.TenantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth/tenants")
public class TenantAccessController {

    private final TenantService tenantService;

    public TenantAccessController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping
    public List<TenantLoginOptionDto> findAvailableTenants(@RequestParam("username") String username) {
        return tenantService.findAvailableTenantsForUsername(username);
    }
}
