package com.erp.qualitascareapi.security.api;

import com.erp.qualitascareapi.security.api.dto.RoleDto;
import com.erp.qualitascareapi.security.api.dto.RoleRequest;
import com.erp.qualitascareapi.security.application.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public Page<RoleDto> list(Pageable pageable) {
        return roleService.list(pageable);
    }

    @GetMapping("/{id}")
    public RoleDto get(@PathVariable Long id) {
        return roleService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleDto create(@Validated @RequestBody RoleRequest request) {
        return roleService.create(request);
    }

    @PutMapping("/{id}")
    public RoleDto update(@PathVariable Long id, @Validated @RequestBody RoleRequest request) {
        return roleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }
}
