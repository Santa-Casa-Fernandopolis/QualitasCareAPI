package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.CmeDashboardDto;
import com.erp.qualitascareapi.cme.application.CmeDashboardService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cme")
public class CmeDashboardController {

    private final CmeDashboardService dashboardService;

    public CmeDashboardController(CmeDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    @RequiresPermission(resource = ResourceType.DASHBOARD, action = Action.READ)
    public CmeDashboardDto getDashboard() {
        return dashboardService.getDashboard();
    }
}
