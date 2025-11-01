package com.erp.qualitascareapi.security.api;

import com.erp.qualitascareapi.security.api.dto.RolePermissionDto;
import com.erp.qualitascareapi.security.api.dto.RolePermissionRequest;
import com.erp.qualitascareapi.security.application.RolePermissionService;
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

    @GetMapping
    public Page<RolePermissionDto> list(Pageable pageable) {
        return rolePermissionService.list(pageable);
    }

    @GetMapping("/{id}")
    public RolePermissionDto get(@PathVariable Long id) {
        return rolePermissionService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RolePermissionDto create(@Validated @RequestBody RolePermissionRequest request) {
        return rolePermissionService.create(request);
    }

    @PutMapping("/{id}")
    public RolePermissionDto update(@PathVariable Long id, @Validated @RequestBody RolePermissionRequest request) {
        return rolePermissionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        rolePermissionService.delete(id);
    }
}
