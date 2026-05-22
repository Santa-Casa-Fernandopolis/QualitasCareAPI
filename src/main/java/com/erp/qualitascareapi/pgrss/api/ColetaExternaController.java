package com.erp.qualitascareapi.pgrss.api;

import com.erp.qualitascareapi.pgrss.api.dto.ColetaExternaDto;
import com.erp.qualitascareapi.pgrss.api.dto.ColetaExternaRequest;
import com.erp.qualitascareapi.pgrss.application.ColetaExternaService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pgrss/coletas-externas")
public class ColetaExternaController {

    private final ColetaExternaService service;

    public ColetaExternaController(ColetaExternaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_COLETA_EXTERNA, action = Action.CREATE)
    public ColetaExternaDto registrar(@Validated @RequestBody ColetaExternaRequest req) {
        return service.registrar(req);
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.PGRSS_COLETA_EXTERNA, action = Action.READ)
    public Page<ColetaExternaDto> findAll(Pageable pageable) {
        return service.search(pageable);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_COLETA_EXTERNA, action = Action.READ)
    public ColetaExternaDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PatchMapping("/{id}/documentada")
    @RequiresPermission(resource = ResourceType.PGRSS_COLETA_EXTERNA, action = Action.UPDATE)
    public ColetaExternaDto marcarDocumentada(@PathVariable Long id) {
        return service.marcarDocumentada(id);
    }

    @PatchMapping("/{id}/cancelar")
    @RequiresPermission(resource = ResourceType.PGRSS_COLETA_EXTERNA, action = Action.UPDATE)
    public ColetaExternaDto cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
