package com.erp.qualitascareapi.iam.api;

import com.erp.qualitascareapi.iam.api.dto.OrgRoleAssignmentDto;
import com.erp.qualitascareapi.iam.api.dto.OrgRoleAssignmentRequest;
import com.erp.qualitascareapi.iam.application.OrgRoleAssignmentService;
import com.erp.qualitascareapi.iam.enums.OrgRoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/org-role-assignments")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class OrgRoleAssignmentController {

    private final OrgRoleAssignmentService orgRoleAssignmentService;

    public OrgRoleAssignmentController(OrgRoleAssignmentService orgRoleAssignmentService) {
        this.orgRoleAssignmentService = orgRoleAssignmentService;
    }

    @GetMapping
    public Page<OrgRoleAssignmentDto> list(@RequestParam(required = false) Long tenantId,
                                           @RequestParam(required = false) OrgRoleType roleType,
                                           @RequestParam(required = false) Boolean active,
                                           Pageable pageable) {
        return orgRoleAssignmentService.list(tenantId, roleType, active, pageable);
    }

    @GetMapping("/{id}")
    public OrgRoleAssignmentDto get(@PathVariable Long id) {
        return orgRoleAssignmentService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrgRoleAssignmentDto create(@Validated @RequestBody OrgRoleAssignmentRequest request) {
        return orgRoleAssignmentService.create(request);
    }

    @PutMapping("/{id}")
    public OrgRoleAssignmentDto update(@PathVariable Long id,
                                       @Validated @RequestBody OrgRoleAssignmentRequest request) {
        return orgRoleAssignmentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        orgRoleAssignmentService.delete(id);
    }
}
