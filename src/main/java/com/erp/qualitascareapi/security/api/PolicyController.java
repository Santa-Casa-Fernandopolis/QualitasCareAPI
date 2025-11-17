package com.erp.qualitascareapi.security.api;

import com.erp.qualitascareapi.security.api.dto.PolicyDto;
import com.erp.qualitascareapi.security.api.dto.PolicyRequest;
import com.erp.qualitascareapi.security.application.PolicyService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @RequiresPermission(resource = ResourceType.SECURITY_POLICY, action = Action.READ)
    @GetMapping
    public Page<PolicyDto> list(Pageable pageable) {
        return policyService.list(pageable);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_POLICY, action = Action.READ)
    @GetMapping("/{id}")
    public PolicyDto get(@PathVariable Long id) {
        return policyService.get(id);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_POLICY, action = Action.CREATE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PolicyDto create(@Validated @RequestBody PolicyRequest request) {
        return policyService.create(request);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_POLICY, action = Action.UPDATE)
    @PutMapping("/{id}")
    public PolicyDto update(@PathVariable Long id, @Validated @RequestBody PolicyRequest request) {
        return policyService.update(id, request);
    }

    @RequiresPermission(resource = ResourceType.SECURITY_POLICY, action = Action.DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        policyService.delete(id);
    }
}
