package com.erp.qualitascareapi.hr.api;

import com.erp.qualitascareapi.hr.api.dto.ColaboradorDto;
import com.erp.qualitascareapi.hr.api.dto.ColaboradorRequest;
import com.erp.qualitascareapi.hr.application.ColaboradorService;
import com.erp.qualitascareapi.hr.enums.ColaboradorStatus;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr/colaboradores")
public class ColaboradorController {

    private final ColaboradorService colaboradorService;

    public ColaboradorController(ColaboradorService colaboradorService) {
        this.colaboradorService = colaboradorService;
    }

    @RequiresPermission(resource = ResourceType.HR_COLABORADOR, action = Action.READ)
    @GetMapping
    public Page<ColaboradorDto> list(@RequestParam(required = false) Long tenantId,
                                     @RequestParam(required = false) ColaboradorStatus status,
                                     Pageable pageable) {
        return colaboradorService.list(tenantId, status, pageable);
    }

    @RequiresPermission(resource = ResourceType.HR_COLABORADOR, action = Action.READ)
    @GetMapping("/{id}")
    public ColaboradorDto get(@PathVariable Long id) {
        return colaboradorService.get(id);
    }

    @RequiresPermission(resource = ResourceType.HR_COLABORADOR, action = Action.CREATE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ColaboradorDto create(@Validated @RequestBody ColaboradorRequest request) {
        return colaboradorService.create(request);
    }

    @RequiresPermission(resource = ResourceType.HR_COLABORADOR, action = Action.UPDATE)
    @PutMapping("/{id}")
    public ColaboradorDto update(@PathVariable Long id, @Validated @RequestBody ColaboradorRequest request) {
        return colaboradorService.update(id, request);
    }

    @RequiresPermission(resource = ResourceType.HR_COLABORADOR, action = Action.DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        colaboradorService.delete(id);
    }
}
