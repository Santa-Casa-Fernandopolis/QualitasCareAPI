package com.erp.qualitascareapi.ged.api;

import com.erp.qualitascareapi.ged.api.dto.DocumentDashboardDto;
import com.erp.qualitascareapi.ged.application.DocumentDashboardService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ged")
public class DocumentDashboardController {

    private final DocumentDashboardService dashboardService;

    public DocumentDashboardController(DocumentDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    @RequiresPermission(resource = ResourceType.DOCUMENTO, action = Action.READ)
    public DocumentDashboardDto getDashboard() {
        return dashboardService.getDashboard();
    }
}
