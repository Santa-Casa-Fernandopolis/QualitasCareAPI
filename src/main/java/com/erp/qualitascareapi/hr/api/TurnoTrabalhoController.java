package com.erp.qualitascareapi.hr.api;

import com.erp.qualitascareapi.hr.api.dto.TurnoTrabalhoDto;
import com.erp.qualitascareapi.hr.api.dto.TurnoTrabalhoRequest;
import com.erp.qualitascareapi.hr.application.TurnoTrabalhoService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr/turnos")
public class TurnoTrabalhoController {

    private final TurnoTrabalhoService turnoService;

    public TurnoTrabalhoController(TurnoTrabalhoService turnoService) {
        this.turnoService = turnoService;
    }

    @RequiresPermission(resource = ResourceType.HR_TURNO, action = Action.READ)
    @GetMapping
    public Page<TurnoTrabalhoDto> list(@RequestParam(required = false) Long tenantId, Pageable pageable) {
        return turnoService.list(tenantId, pageable);
    }

    @RequiresPermission(resource = ResourceType.HR_TURNO, action = Action.READ)
    @GetMapping("/{id}")
    public TurnoTrabalhoDto get(@PathVariable Long id) {
        return turnoService.get(id);
    }

    @RequiresPermission(resource = ResourceType.HR_TURNO, action = Action.CREATE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TurnoTrabalhoDto create(@Validated @RequestBody TurnoTrabalhoRequest request) {
        return turnoService.create(request);
    }

    @RequiresPermission(resource = ResourceType.HR_TURNO, action = Action.UPDATE)
    @PutMapping("/{id}")
    public TurnoTrabalhoDto update(@PathVariable Long id, @Validated @RequestBody TurnoTrabalhoRequest request) {
        return turnoService.update(id, request);
    }

    @RequiresPermission(resource = ResourceType.HR_TURNO, action = Action.DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        turnoService.delete(id);
    }
}
