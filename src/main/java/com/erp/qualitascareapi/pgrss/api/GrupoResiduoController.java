package com.erp.qualitascareapi.pgrss.api;

import com.erp.qualitascareapi.pgrss.api.dto.*;
import com.erp.qualitascareapi.pgrss.application.GrupoTipoResiduoService;
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
@RequestMapping("/api/pgrss")
public class GrupoResiduoController {

    private final GrupoTipoResiduoService service;

    public GrupoResiduoController(GrupoTipoResiduoService service) {
        this.service = service;
    }

    @PostMapping("/grupos")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.CREATE)
    public GrupoResiduoDto createGrupo(@Validated @RequestBody GrupoResiduoRequest req) {
        return service.createGrupo(req);
    }

    @GetMapping("/grupos")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public Page<GrupoResiduoDto> listGrupos(Pageable pageable) {
        return service.listGrupos(pageable);
    }

    @GetMapping("/grupos/ativos")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public List<GrupoResiduoDto> listGruposAtivos() {
        return service.listGruposAtivos();
    }

    @GetMapping("/grupos/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public GrupoResiduoDto getGrupo(@PathVariable Long id) {
        return service.findGrupoById(id);
    }

    @PutMapping("/grupos/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.UPDATE)
    public GrupoResiduoDto updateGrupo(@PathVariable Long id, @Validated @RequestBody GrupoResiduoRequest req) {
        return service.updateGrupo(id, req);
    }

    @PatchMapping("/grupos/{id}/ativo")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.UPDATE)
    public GrupoResiduoDto toggleGrupoAtivo(@PathVariable Long id) {
        return service.toggleGrupoAtivo(id);
    }

    @PostMapping("/tipos")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.CREATE)
    public TipoResiduoDto createTipo(@Validated @RequestBody TipoResiduoRequest req) {
        return service.createTipo(req);
    }

    @GetMapping("/tipos")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public Page<TipoResiduoDto> listTipos(Pageable pageable) {
        return service.listTipos(pageable);
    }

    @GetMapping("/tipos/por-grupo/{grupoId}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public List<TipoResiduoDto> listTiposByGrupo(@PathVariable Long grupoId) {
        return service.listTiposByGrupo(grupoId);
    }

    @GetMapping("/tipos/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public TipoResiduoDto getTipo(@PathVariable Long id) {
        return service.findTipoById(id);
    }

    @PutMapping("/tipos/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.UPDATE)
    public TipoResiduoDto updateTipo(@PathVariable Long id, @Validated @RequestBody TipoResiduoRequest req) {
        return service.updateTipo(id, req);
    }

    @PatchMapping("/tipos/{id}/ativo")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.UPDATE)
    public TipoResiduoDto toggleTipoAtivo(@PathVariable Long id) {
        return service.toggleTipoAtivo(id);
    }
}
