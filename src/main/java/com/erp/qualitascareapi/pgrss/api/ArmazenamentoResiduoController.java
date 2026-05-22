package com.erp.qualitascareapi.pgrss.api;

import com.erp.qualitascareapi.pgrss.api.dto.ArmazenamentoResiduoDto;
import com.erp.qualitascareapi.pgrss.api.dto.ArmazenamentoResiduoRequest;
import com.erp.qualitascareapi.pgrss.application.ArmazenamentoResiduoService;
import com.erp.qualitascareapi.pgrss.enums.StatusArmazenamento;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pgrss/armazenamentos")
public class ArmazenamentoResiduoController {

    private final ArmazenamentoResiduoService service;

    public ArmazenamentoResiduoController(ArmazenamentoResiduoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.CREATE)
    public ArmazenamentoResiduoDto registrar(@Validated @RequestBody ArmazenamentoResiduoRequest req) {
        return service.registrar(req);
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.READ)
    public Page<ArmazenamentoResiduoDto> findAll(@RequestParam(required = false) Long grupoId,
                                                  @RequestParam(required = false) StatusArmazenamento status,
                                                  Pageable pageable) {
        return service.search(grupoId, status, pageable);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.READ)
    public ArmazenamentoResiduoDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PatchMapping("/{id}/remover")
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.UPDATE)
    public ArmazenamentoResiduoDto remover(@PathVariable Long id) {
        return service.remover(id);
    }

    @PatchMapping("/{id}/cancelar")
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.UPDATE)
    public ArmazenamentoResiduoDto cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
