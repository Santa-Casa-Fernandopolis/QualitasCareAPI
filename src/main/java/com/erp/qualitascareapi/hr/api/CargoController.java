package com.erp.qualitascareapi.hr.api;

import com.erp.qualitascareapi.hr.api.dto.CargoDto;
import com.erp.qualitascareapi.hr.api.dto.CargoRequest;
import com.erp.qualitascareapi.hr.application.CargoService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr/cargos")
public class CargoController {

    private final CargoService cargoService;

    public CargoController(CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @RequiresPermission(resource = ResourceType.HR_CARGO, action = Action.READ)
    @GetMapping
    public Page<CargoDto> list(@RequestParam(required = false) Long tenantId, Pageable pageable) {
        return cargoService.list(tenantId, pageable);
    }

    @RequiresPermission(resource = ResourceType.HR_CARGO, action = Action.READ)
    @GetMapping("/{id}")
    public CargoDto get(@PathVariable Long id) {
        return cargoService.get(id);
    }

    @RequiresPermission(resource = ResourceType.HR_CARGO, action = Action.CREATE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CargoDto create(@Validated @RequestBody CargoRequest request) {
        return cargoService.create(request);
    }

    @RequiresPermission(resource = ResourceType.HR_CARGO, action = Action.UPDATE)
    @PutMapping("/{id}")
    public CargoDto update(@PathVariable Long id, @Validated @RequestBody CargoRequest request) {
        return cargoService.update(id, request);
    }

    @RequiresPermission(resource = ResourceType.HR_CARGO, action = Action.DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        cargoService.delete(id);
    }
}
