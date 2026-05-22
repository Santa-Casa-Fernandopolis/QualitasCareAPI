package com.erp.qualitascareapi.pgrss.api;

import com.erp.qualitascareapi.pgrss.api.dto.NaoConformidadeResiduoDto;
import com.erp.qualitascareapi.pgrss.api.dto.NaoConformidadeResiduoRequest;
import com.erp.qualitascareapi.pgrss.application.NaoConformidadeResiduoService;
import com.erp.qualitascareapi.pgrss.enums.SeveridadeNaoConformidade;
import com.erp.qualitascareapi.pgrss.enums.StatusNaoConformidade;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pgrss/nao-conformidades")
public class NaoConformidadeResiduoController {

    private final NaoConformidadeResiduoService service;

    public NaoConformidadeResiduoController(NaoConformidadeResiduoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_NAO_CONFORMIDADE, action = Action.CREATE)
    public NaoConformidadeResiduoDto registrar(@Validated @RequestBody NaoConformidadeResiduoRequest req) {
        return service.registrar(req);
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.PGRSS_NAO_CONFORMIDADE, action = Action.READ)
    public Page<NaoConformidadeResiduoDto> search(@RequestParam(required = false) Long setorId,
                                                   @RequestParam(required = false) SeveridadeNaoConformidade severidade,
                                                   @RequestParam(required = false) StatusNaoConformidade status,
                                                   Pageable pageable) {
        return service.search(setorId, severidade, status, pageable);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_NAO_CONFORMIDADE, action = Action.READ)
    public NaoConformidadeResiduoDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PatchMapping("/{id}/status")
    @RequiresPermission(resource = ResourceType.PGRSS_NAO_CONFORMIDADE, action = Action.UPDATE)
    public NaoConformidadeResiduoDto atualizarStatus(@PathVariable Long id,
                                                      @RequestParam StatusNaoConformidade status) {
        return service.atualizarStatus(id, status);
    }
}
