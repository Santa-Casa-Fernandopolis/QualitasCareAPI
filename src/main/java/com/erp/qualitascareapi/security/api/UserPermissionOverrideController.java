package com.erp.qualitascareapi.security.api;

import com.erp.qualitascareapi.security.api.dto.UserPermissionOverrideDto;
import com.erp.qualitascareapi.security.api.dto.UserPermissionOverrideRequest;
import com.erp.qualitascareapi.security.application.UserPermissionOverrideService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-overrides")
public class UserPermissionOverrideController {

    private final UserPermissionOverrideService overrideService;

    public UserPermissionOverrideController(UserPermissionOverrideService overrideService) {
        this.overrideService = overrideService;
    }

    @RequiresPermission(resource = ResourceType.SECURITY_USER_PERMISSION_OVERRIDE, action = Action.READ)
    @GetMapping
    public Page<UserPermissionOverrideDto> list(Pageable pageable) {
        return overrideService.list(pageable);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_USER_PERMISSION_OVERRIDE, action = Action.READ)
    @GetMapping("/{id}")
    public UserPermissionOverrideDto get(@PathVariable Long id) {
        return overrideService.get(id);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_USER_PERMISSION_OVERRIDE, action = Action.CREATE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserPermissionOverrideDto create(@Validated @RequestBody UserPermissionOverrideRequest request) {
        return overrideService.create(request);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_USER_PERMISSION_OVERRIDE, action = Action.UPDATE)
    @PutMapping("/{id}")
    public UserPermissionOverrideDto update(@PathVariable Long id, @Validated @RequestBody UserPermissionOverrideRequest request) {
        return overrideService.update(id, request);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_USER_PERMISSION_OVERRIDE, action = Action.DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        overrideService.delete(id);
    }
}
