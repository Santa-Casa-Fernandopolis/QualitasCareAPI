package com.erp.qualitascareapi.environmental.api;

import com.erp.qualitascareapi.environmental.api.dto.AmbienteDto;
import com.erp.qualitascareapi.environmental.api.dto.AmbienteRequest;
import com.erp.qualitascareapi.environmental.application.EnvironmentalService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/env/ambientes")
public class AmbienteController {

    private final EnvironmentalService environmentalService;

    public AmbienteController(EnvironmentalService environmentalService) {
        this.environmentalService = environmentalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.ENV_AMBIENTE, action = Action.CREATE)
    public AmbienteDto cadastrarAmbiente(@Validated @RequestBody AmbienteRequest request) {
        return environmentalService.cadastrarAmbiente(request);
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = ResourceType.ENV_AMBIENTE, action = Action.UPDATE)
    public AmbienteDto updateAmbiente(@PathVariable Long id,
                                      @Validated @RequestBody AmbienteRequest request) {
        return environmentalService.updateAmbiente(id, request);
    }

    @PatchMapping("/{id}/status")
    @RequiresPermission(resource = ResourceType.ENV_AMBIENTE, action = Action.UPDATE)
    public AmbienteDto toggleStatus(@PathVariable Long id, @RequestParam boolean ativo) {
        return environmentalService.toggleAmbienteStatus(id, ativo);
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.ENV_AMBIENTE, action = Action.READ)
    public Page<AmbienteDto> listAmbientes(@RequestParam(required = false) Boolean ativo,
                                           Pageable pageable) {
        return environmentalService.listAmbientes(ativo, pageable);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.ENV_AMBIENTE, action = Action.READ)
    public AmbienteDto getAmbiente(@PathVariable Long id) {
        return environmentalService.findAmbienteById(id);
    }
}
