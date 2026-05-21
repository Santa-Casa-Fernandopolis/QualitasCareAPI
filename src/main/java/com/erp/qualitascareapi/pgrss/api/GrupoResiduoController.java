package com.erp.qualitascareapi.pgrss.api;

import com.erp.qualitascareapi.pgrss.api.dto.GrupoResiduoDto;
import com.erp.qualitascareapi.pgrss.api.dto.GrupoResiduoRequest;
import com.erp.qualitascareapi.pgrss.application.GrupoResiduoService;
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
@RequestMapping("/api/pgrss/grupos-residuo")
public class GrupoResiduoController {

    private final GrupoResiduoService service;

    public GrupoResiduoController(GrupoResiduoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.CREATE)
    public GrupoResiduoDto create(@Validated @RequestBody GrupoResiduoRequest request) {
        return service.create(request);
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public Page<GrupoResiduoDto> list(Pageable pageable) {
        return service.list(pageable);
    }

    @GetMapping("/ativos")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public List<GrupoResiduoDto> listAtivos() {
        return service.listAtivos();
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public GrupoResiduoDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.UPDATE)
    public GrupoResiduoDto update(@PathVariable Long id, @Validated @RequestBody GrupoResiduoRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.UPDATE)
    public GrupoResiduoDto toggleAtivo(@PathVariable Long id, @RequestParam Boolean ativo) {
        return service.toggleAtivo(id, ativo);
    }
}
