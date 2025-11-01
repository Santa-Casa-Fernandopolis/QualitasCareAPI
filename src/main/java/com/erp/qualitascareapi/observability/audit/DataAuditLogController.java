package com.erp.qualitascareapi.observability.audit;

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
    public List<DataAuditEntry> getRevisions(@PathVariable String entityAlias, @PathVariable String id) {
        return service.getRevisions(entityAlias, id);
    }
}
