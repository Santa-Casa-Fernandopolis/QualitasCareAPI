package com.erp.qualitascareapi.same.api;

import com.erp.qualitascareapi.same.api.dto.SameDocumentAccessLogDto;
import com.erp.qualitascareapi.same.application.SameDocumentAuditService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/same/audit")
public class SameAuditController {

    private final SameDocumentAuditService auditService;

    public SameAuditController(SameDocumentAuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/documents/{documentId}")
    @RequiresPermission(resource = ResourceType.SAME_AUDIT, action = Action.READ)
    public List<SameDocumentAccessLogDto> listByDocument(@PathVariable Long documentId) {
        return auditService.listByDocument(documentId);
    }

    @GetMapping("/patients/{patientId}")
    @RequiresPermission(resource = ResourceType.SAME_AUDIT, action = Action.READ)
    public List<SameDocumentAccessLogDto> listByPatient(@PathVariable Long patientId) {
        return auditService.listByPatient(patientId);
    }
}
