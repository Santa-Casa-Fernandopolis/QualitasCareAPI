package com.erp.qualitascareapi.iam.api;

import com.erp.qualitascareapi.iam.api.dto.OrgRoleAssignmentDto;
import com.erp.qualitascareapi.iam.api.dto.OrgRoleAssignmentRequest;
import com.erp.qualitascareapi.iam.application.OrgRoleAssignmentService;
import com.erp.qualitascareapi.iam.enums.OrgRoleType;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/org-role-assignments")
public class OrgRoleAssignmentController {

    private final OrgRoleAssignmentService orgRoleAssignmentService;

    public OrgRoleAssignmentController(OrgRoleAssignmentService orgRoleAssignmentService) {
        this.orgRoleAssignmentService = orgRoleAssignmentService;
    }

    @RequiresPermission(resource = ResourceType.IAM_ORG_ROLE_ASSIGNMENT, action = Action.READ)
    @GetMapping
    public Page<OrgRoleAssignmentDto> list(@RequestParam(required = false) Long tenantId,
                                           @RequestParam(required = false) OrgRoleType roleType,
                                           @RequestParam(required = false) Boolean active,
                                           Pageable pageable) {
        return orgRoleAssignmentService.list(tenantId, roleType, active, pageable);
    }

    @RequiresPermission(resource = ResourceType.IAM_ORG_ROLE_ASSIGNMENT, action = Action.READ)
    @GetMapping("/{id}")
    public OrgRoleAssignmentDto get(@PathVariable Long id) {
        return orgRoleAssignmentService.get(id);
    }

    @RequiresPermission(resource = ResourceType.IAM_ORG_ROLE_ASSIGNMENT, action = Action.CREATE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrgRoleAssignmentDto create(@Validated @RequestBody OrgRoleAssignmentRequest request) {
        return orgRoleAssignmentService.create(request);
    }

    @RequiresPermission(resource = ResourceType.IAM_ORG_ROLE_ASSIGNMENT, action = Action.UPDATE)
    @PutMapping("/{id}")
    public OrgRoleAssignmentDto update(@PathVariable Long id,
                                       @Validated @RequestBody OrgRoleAssignmentRequest request) {
        return orgRoleAssignmentService.update(id, request);
    }

    @RequiresPermission(resource = ResourceType.IAM_ORG_ROLE_ASSIGNMENT, action = Action.DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        orgRoleAssignmentService.delete(id);
    }
}
