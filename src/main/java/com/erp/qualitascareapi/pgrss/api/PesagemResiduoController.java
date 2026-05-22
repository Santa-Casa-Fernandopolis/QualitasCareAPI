package com.erp.qualitascareapi.pgrss.api;

import com.erp.qualitascareapi.pgrss.api.dto.PesagemFiltroRequest;
import com.erp.qualitascareapi.pgrss.api.dto.PesagemResiduoDto;
import com.erp.qualitascareapi.pgrss.api.dto.PesagemResiduoRequest;
import com.erp.qualitascareapi.pgrss.application.PesagemResiduoService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pgrss/pesagens")
public class PesagemResiduoController {

    private final PesagemResiduoService service;

    public PesagemResiduoController(PesagemResiduoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.CREATE)
    public PesagemResiduoDto registrar(@Validated @RequestBody PesagemResiduoRequest req) {
        return service.registrar(req);
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.READ)
    public Page<PesagemResiduoDto> search(@ModelAttribute PesagemFiltroRequest filtro, Pageable pageable) {
        return service.search(filtro, pageable);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.READ)
    public PesagemResiduoDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PatchMapping("/{id}/validar")
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.UPDATE)
    public PesagemResiduoDto validar(@PathVariable Long id) {
        return service.validar(id);
    }

    @PatchMapping("/{id}/cancelar")
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.UPDATE)
    public PesagemResiduoDto cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
