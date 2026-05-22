package com.erp.qualitascareapi.pgrss.api;

import com.erp.qualitascareapi.pgrss.api.dto.SetorGeradorDto;
import com.erp.qualitascareapi.pgrss.api.dto.SetorGeradorRequest;
import com.erp.qualitascareapi.pgrss.application.SetorGeradorService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pgrss/setores")
public class SetorGeradorController {

    private final SetorGeradorService service;

    public SetorGeradorController(SetorGeradorService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.CREATE)
    public SetorGeradorDto create(@Validated @RequestBody SetorGeradorRequest req) {
        return service.create(req);
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public Page<SetorGeradorDto> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public SetorGeradorDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.UPDATE)
    public SetorGeradorDto update(@PathVariable Long id, @Validated @RequestBody SetorGeradorRequest req) {
        return service.update(id, req);
    }

    @PatchMapping("/{id}/ativo")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.UPDATE)
    public SetorGeradorDto toggleAtivo(@PathVariable Long id) {
        return service.toggleAtivo(id);
    }
}
