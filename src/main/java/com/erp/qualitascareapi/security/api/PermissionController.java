package com.erp.qualitascareapi.security.api;

import com.erp.qualitascareapi.security.api.dto.PermissionDto;
import com.erp.qualitascareapi.security.api.dto.PermissionRequest;
import com.erp.qualitascareapi.security.application.PermissionService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @RequiresPermission(resource = ResourceType.SECURITY_PERMISSION, action = Action.READ)
    @GetMapping
    public Page<PermissionDto> list(Pageable pageable) {
        return permissionService.list(pageable);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_PERMISSION, action = Action.READ)
    @GetMapping("/{id}")
    public PermissionDto get(@PathVariable Long id) {
        return permissionService.get(id);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_PERMISSION, action = Action.CREATE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PermissionDto create(@Validated @RequestBody PermissionRequest request) {
        return permissionService.create(request);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_PERMISSION, action = Action.UPDATE)
    @PutMapping("/{id}")
    public PermissionDto update(@PathVariable Long id, @Validated @RequestBody PermissionRequest request) {
        return permissionService.update(id, request);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_PERMISSION, action = Action.DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        permissionService.delete(id);
    }
}
