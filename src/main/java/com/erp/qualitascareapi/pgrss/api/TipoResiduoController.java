package com.erp.qualitascareapi.pgrss.api;

import com.erp.qualitascareapi.pgrss.api.dto.TipoResiduoDto;
import com.erp.qualitascareapi.pgrss.api.dto.TipoResiduoRequest;
import com.erp.qualitascareapi.pgrss.application.TipoResiduoService;
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
@RequestMapping("/api/pgrss/tipos-residuo")
public class TipoResiduoController {

    private final TipoResiduoService service;

    public TipoResiduoController(TipoResiduoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.CREATE)
    public TipoResiduoDto create(@Validated @RequestBody TipoResiduoRequest request) {
        return service.create(request);
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public Page<TipoResiduoDto> list(@RequestParam(required = false) Boolean ativo, Pageable pageable) {
        return service.list(ativo, pageable);
    }

    @GetMapping("/por-grupo/{grupoId}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public List<TipoResiduoDto> listByGrupo(@PathVariable Long grupoId) {
        return service.listByGrupo(grupoId);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public TipoResiduoDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.UPDATE)
    public TipoResiduoDto update(@PathVariable Long id, @Validated @RequestBody TipoResiduoRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.UPDATE)
    public TipoResiduoDto toggleAtivo(@PathVariable Long id, @RequestParam Boolean ativo) {
        return service.toggleAtivo(id, ativo);
    }
}
