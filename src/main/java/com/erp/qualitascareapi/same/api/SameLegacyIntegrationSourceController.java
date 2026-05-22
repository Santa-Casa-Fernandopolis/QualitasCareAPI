package com.erp.qualitascareapi.same.api;

import com.erp.qualitascareapi.same.api.dto.SameLegacyConnectionTestDto;
import com.erp.qualitascareapi.same.api.dto.SameLegacyIntegrationSourceDto;
import com.erp.qualitascareapi.same.api.dto.SameLegacyIntegrationSourceRequest;
import com.erp.qualitascareapi.same.api.dto.SameLegacyIntegrationSourceStatusRequest;
import com.erp.qualitascareapi.same.application.SameLegacyIntegrationSourceService;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/same/legacy-sources")
public class SameLegacyIntegrationSourceController {

    private final SameLegacyIntegrationSourceService service;

    public SameLegacyIntegrationSourceController(SameLegacyIntegrationSourceService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.SAME_LEGACY_SOURCE, action = Action.CREATE)
    public SameLegacyIntegrationSourceDto create(@Valid @RequestBody SameLegacyIntegrationSourceRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = ResourceType.SAME_LEGACY_SOURCE, action = Action.UPDATE)
    public SameLegacyIntegrationSourceDto update(@PathVariable Long id,
                                                 @Valid @RequestBody SameLegacyIntegrationSourceRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @RequiresPermission(resource = ResourceType.SAME_LEGACY_SOURCE, action = Action.UPDATE)
    public SameLegacyIntegrationSourceDto updateStatus(@PathVariable Long id,
                                                       @Valid @RequestBody SameLegacyIntegrationSourceStatusRequest request) {
        return service.updateStatus(id, request.active());
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.SAME_LEGACY_SOURCE, action = Action.READ)
    public SameLegacyIntegrationSourceDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.SAME_LEGACY_SOURCE, action = Action.READ)
    public Page<SameLegacyIntegrationSourceDto> list(@RequestParam(required = false) SameSourceSystem sourceSystem,
                                                     @RequestParam(required = false) Boolean active,
                                                     Pageable pageable) {
        return service.list(sourceSystem, active, pageable);
    }

    @PostMapping("/{id}/test-connection")
    @RequiresPermission(resource = ResourceType.SAME_LEGACY_SOURCE, action = Action.UPDATE)
    public SameLegacyConnectionTestDto testConnection(@PathVariable Long id) {
        return service.testConnection(id);
    }
}
