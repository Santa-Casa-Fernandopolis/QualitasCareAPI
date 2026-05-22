package com.erp.qualitascareapi.pgrss.api;

import com.erp.qualitascareapi.pgrss.api.dto.ColetaInternaDto;
import com.erp.qualitascareapi.pgrss.api.dto.ColetaInternaRequest;
import com.erp.qualitascareapi.pgrss.application.ColetaResiduoService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pgrss/coletas-internas")
public class ColetaInternaController {

    private final ColetaResiduoService service;

    public ColetaInternaController(ColetaResiduoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.CREATE)
    public ColetaInternaDto iniciar(@Validated @RequestBody ColetaInternaRequest req) {
        return service.iniciarColeta(req);
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.READ)
    public Page<ColetaInternaDto> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.READ)
    public ColetaInternaDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping("/{id}/pesagens/{pesagemId}")
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.UPDATE)
    public ColetaInternaDto vincularPesagem(@PathVariable Long id, @PathVariable Long pesagemId) {
        return service.vincularPesagem(id, pesagemId);
    }

    @PatchMapping("/{id}/finalizar")
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.UPDATE)
    public ColetaInternaDto finalizar(@PathVariable Long id) {
        return service.finalizarColeta(id);
    }

    @PatchMapping("/{id}/cancelar")
    @RequiresPermission(resource = ResourceType.PGRSS_PESAGEM, action = Action.UPDATE)
    public ColetaInternaDto cancelar(@PathVariable Long id) {
        return service.cancelarColeta(id);
    }
}
