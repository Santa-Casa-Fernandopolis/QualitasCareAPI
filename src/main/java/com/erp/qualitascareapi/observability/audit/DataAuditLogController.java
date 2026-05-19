package com.erp.qualitascareapi.observability.audit;

import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs/data")
public class DataAuditLogController {

    private final DataAuditQueryService service;

    public DataAuditLogController(DataAuditQueryService service) {
        this.service = service;
    }

    @GetMapping("/{entityAlias}/{id}")
    @RequiresPermission(resource = ResourceType.OBSERVABILITY_AUDIT, action = Action.READ)
    public List<DataAuditEntry> getRevisions(@PathVariable String entityAlias, @PathVariable String id) {
        return service.getRevisions(entityAlias, id);
    }
}
