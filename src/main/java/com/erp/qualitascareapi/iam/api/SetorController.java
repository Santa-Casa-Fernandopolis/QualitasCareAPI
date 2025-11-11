package com.erp.qualitascareapi.iam.api;

import com.erp.qualitascareapi.iam.api.dto.SetorDto;
import com.erp.qualitascareapi.iam.api.dto.SetorRequest;
import com.erp.qualitascareapi.iam.application.SetorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/setores")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class SetorController {

    private final SetorService setorService;

    public SetorController(SetorService setorService) {
        this.setorService = setorService;
    }

    @GetMapping
    public Page<SetorDto> list(@RequestParam(required = false) Long tenantId, Pageable pageable) {
        return setorService.list(tenantId, pageable);
    }

    @GetMapping("/{id}")
    public SetorDto get(@PathVariable Long id) {
        return setorService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SetorDto create(@Validated @RequestBody SetorRequest request) {
        return setorService.create(request);
    }

    @PutMapping("/{id}")
    public SetorDto update(@PathVariable Long id, @Validated @RequestBody SetorRequest request) {
        return setorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        setorService.delete(id);
    }
}
