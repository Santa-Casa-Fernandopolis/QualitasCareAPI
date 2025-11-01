package com.erp.qualitascareapi.security.api;

import com.erp.qualitascareapi.security.api.dto.PolicyDto;
import com.erp.qualitascareapi.security.api.dto.PolicyRequest;
import com.erp.qualitascareapi.security.application.PolicyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping
    public Page<PolicyDto> list(Pageable pageable) {
        return policyService.list(pageable);
    }

    @GetMapping("/{id}")
    public PolicyDto get(@PathVariable Long id) {
        return policyService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PolicyDto create(@Validated @RequestBody PolicyRequest request) {
        return policyService.create(request);
    }

    @PutMapping("/{id}")
    public PolicyDto update(@PathVariable Long id, @Validated @RequestBody PolicyRequest request) {
        return policyService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        policyService.delete(id);
    }
}
