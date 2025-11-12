package com.erp.qualitascareapi.hr.api;

import com.erp.qualitascareapi.hr.api.dto.ColaboradorDto;
import com.erp.qualitascareapi.hr.api.dto.ColaboradorRequest;
import com.erp.qualitascareapi.hr.application.ColaboradorService;
import com.erp.qualitascareapi.hr.enums.ColaboradorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr/colaboradores")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class ColaboradorController {

    private final ColaboradorService colaboradorService;

    public ColaboradorController(ColaboradorService colaboradorService) {
        this.colaboradorService = colaboradorService;
    }

    @GetMapping
    public Page<ColaboradorDto> list(@RequestParam(required = false) Long tenantId,
                                     @RequestParam(required = false) ColaboradorStatus status,
                                     Pageable pageable) {
        return colaboradorService.list(tenantId, status, pageable);
    }

    @GetMapping("/{id}")
    public ColaboradorDto get(@PathVariable Long id) {
        return colaboradorService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ColaboradorDto create(@Validated @RequestBody ColaboradorRequest request) {
        return colaboradorService.create(request);
    }

    @PutMapping("/{id}")
    public ColaboradorDto update(@PathVariable Long id, @Validated @RequestBody ColaboradorRequest request) {
        return colaboradorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        colaboradorService.delete(id);
    }
}
