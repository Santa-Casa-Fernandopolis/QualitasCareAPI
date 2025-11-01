package com.erp.qualitascareapi.security.api;

import com.erp.qualitascareapi.security.api.dto.UserPermissionOverrideDto;
import com.erp.qualitascareapi.security.api.dto.UserPermissionOverrideRequest;
import com.erp.qualitascareapi.security.application.UserPermissionOverrideService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-overrides")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class UserPermissionOverrideController {

    private final UserPermissionOverrideService overrideService;

    public UserPermissionOverrideController(UserPermissionOverrideService overrideService) {
        this.overrideService = overrideService;
    }

    @GetMapping
    public Page<UserPermissionOverrideDto> list(Pageable pageable) {
        return overrideService.list(pageable);
    }

    @GetMapping("/{id}")
    public UserPermissionOverrideDto get(@PathVariable Long id) {
        return overrideService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserPermissionOverrideDto create(@Validated @RequestBody UserPermissionOverrideRequest request) {
        return overrideService.create(request);
    }

    @PutMapping("/{id}")
    public UserPermissionOverrideDto update(@PathVariable Long id, @Validated @RequestBody UserPermissionOverrideRequest request) {
        return overrideService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        overrideService.delete(id);
    }
}
