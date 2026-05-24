package com.erp.qualitascareapi.iam.api;

import com.erp.qualitascareapi.iam.api.dto.SetorEspecialidadeDto;
import com.erp.qualitascareapi.iam.api.dto.SetorEspecialidadeRequest;
import com.erp.qualitascareapi.iam.api.dto.SetorTipoDto;
import com.erp.qualitascareapi.iam.api.dto.SetorTipoRequest;
import com.erp.qualitascareapi.iam.application.SetorCatalogoService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SetorCatalogoController {

    private final SetorCatalogoService setorCatalogoService;

    public SetorCatalogoController(SetorCatalogoService setorCatalogoService) {
        this.setorCatalogoService = setorCatalogoService;
    }

    @RequiresPermission(resource = ResourceType.IAM_SETOR, action = Action.READ)
    @GetMapping("/api/setor-tipos")
    public Page<SetorTipoDto> listTipos(@RequestParam(required = false) Long tenantId, Pageable pageable) {
        return setorCatalogoService.listTipos(tenantId, pageable);
    }

    @RequiresPermission(resource = ResourceType.IAM_SETOR, action = Action.READ)
    @GetMapping("/api/setor-tipos/ativos")
    public List<SetorTipoDto> listTiposAtivos(@RequestParam(required = false) Long tenantId) {
        return setorCatalogoService.listTiposAtivos(tenantId);
    }

    @RequiresPermission(resource = ResourceType.IAM_SETOR, action = Action.CREATE)
    @PostMapping("/api/setor-tipos")
    @ResponseStatus(HttpStatus.CREATED)
    public SetorTipoDto createTipo(@Validated @RequestBody SetorTipoRequest request) {
        return setorCatalogoService.createTipo(request);
    }

    @RequiresPermission(resource = ResourceType.IAM_SETOR, action = Action.UPDATE)
    @PutMapping("/api/setor-tipos/{id}")
    public SetorTipoDto updateTipo(@PathVariable Long id, @Validated @RequestBody SetorTipoRequest request) {
        return setorCatalogoService.updateTipo(id, request);
    }

    @RequiresPermission(resource = ResourceType.IAM_SETOR, action = Action.DELETE)
    @DeleteMapping("/api/setor-tipos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTipo(@PathVariable Long id) {
        setorCatalogoService.deleteTipo(id);
    }

    @RequiresPermission(resource = ResourceType.IAM_SETOR, action = Action.READ)
    @GetMapping("/api/setor-especialidades")
    public Page<SetorEspecialidadeDto> listEspecialidades(@RequestParam(required = false) Long tenantId, Pageable pageable) {
        return setorCatalogoService.listEspecialidades(tenantId, pageable);
    }

    @RequiresPermission(resource = ResourceType.IAM_SETOR, action = Action.READ)
    @GetMapping("/api/setor-especialidades/ativos")
    public List<SetorEspecialidadeDto> listEspecialidadesAtivas(@RequestParam(required = false) Long tenantId) {
        return setorCatalogoService.listEspecialidadesAtivas(tenantId);
    }

    @RequiresPermission(resource = ResourceType.IAM_SETOR, action = Action.CREATE)
    @PostMapping("/api/setor-especialidades")
    @ResponseStatus(HttpStatus.CREATED)
    public SetorEspecialidadeDto createEspecialidade(@Validated @RequestBody SetorEspecialidadeRequest request) {
        return setorCatalogoService.createEspecialidade(request);
    }

    @RequiresPermission(resource = ResourceType.IAM_SETOR, action = Action.UPDATE)
    @PutMapping("/api/setor-especialidades/{id}")
    public SetorEspecialidadeDto updateEspecialidade(@PathVariable Long id, @Validated @RequestBody SetorEspecialidadeRequest request) {
        return setorCatalogoService.updateEspecialidade(id, request);
    }

    @RequiresPermission(resource = ResourceType.IAM_SETOR, action = Action.DELETE)
    @DeleteMapping("/api/setor-especialidades/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEspecialidade(@PathVariable Long id) {
        setorCatalogoService.deleteEspecialidade(id);
    }
}

