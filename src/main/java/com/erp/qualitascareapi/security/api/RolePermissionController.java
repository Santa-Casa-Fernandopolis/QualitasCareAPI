package com.erp.qualitascareapi.security.api;

import com.erp.qualitascareapi.security.api.dto.RolePermissionDto;
import com.erp.qualitascareapi.security.api.dto.RolePermissionRequest;
import com.erp.qualitascareapi.security.application.RolePermissionService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role-permissions")
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    public RolePermissionController(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    @RequiresPermission(resource = ResourceType.SECURITY_ROLE_PERMISSION, action = Action.READ)
    @GetMapping
    public Page<RolePermissionDto> list(@RequestParam(required = false) Long roleId,
                                        @RequestParam(required = false) Long permissionId,
                                        Pageable pageable) {
        return rolePermissionService.list(roleId, permissionId, pageable);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_ROLE_PERMISSION, action = Action.READ)
    @GetMapping("/{id}")
    public RolePermissionDto get(@PathVariable Long id) {
        return rolePermissionService.get(id);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_ROLE_PERMISSION, action = Action.CREATE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RolePermissionDto create(@Validated @RequestBody RolePermissionRequest request) {
        return rolePermissionService.create(request);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_ROLE_PERMISSION, action = Action.UPDATE)
    @PutMapping("/{id}")
    public RolePermissionDto update(@PathVariable Long id, @Validated @RequestBody RolePermissionRequest request) {
        return rolePermissionService.update(id, request);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_ROLE_PERMISSION, action = Action.DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        rolePermissionService.delete(id);
    }
}
