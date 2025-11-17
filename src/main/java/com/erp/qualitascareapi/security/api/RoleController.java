package com.erp.qualitascareapi.security.api;

import com.erp.qualitascareapi.security.api.dto.RoleDto;
import com.erp.qualitascareapi.security.api.dto.RoleRequest;
import com.erp.qualitascareapi.security.application.RoleService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @RequiresPermission(resource = ResourceType.SECURITY_ROLE, action = Action.READ)
    @GetMapping
    public Page<RoleDto> list(@RequestParam(required = false) String name,
                              @RequestParam(required = false) String description,
                              Pageable pageable) {
        return roleService.list(name, description, pageable);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_ROLE, action = Action.READ)
    @GetMapping("/{id}")
    public RoleDto get(@PathVariable Long id) {
        return roleService.get(id);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_ROLE, action = Action.CREATE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleDto create(@Validated @RequestBody RoleRequest request) {
        return roleService.create(request);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_ROLE, action = Action.UPDATE)
    @PutMapping("/{id}")
    public RoleDto update(@PathVariable Long id, @Validated @RequestBody RoleRequest request) {
        return roleService.update(id, request);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_ROLE, action = Action.DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }
}
