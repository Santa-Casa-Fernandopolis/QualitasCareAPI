package com.erp.qualitascareapi.pgrss.api;

import com.erp.qualitascareapi.pgrss.api.dto.PlanoAcaoConcluirRequest;
import com.erp.qualitascareapi.pgrss.api.dto.PlanoAcaoResiduoDto;
import com.erp.qualitascareapi.pgrss.api.dto.PlanoAcaoResiduoRequest;
import com.erp.qualitascareapi.pgrss.application.PlanoAcaoResiduoService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pgrss")
public class PlanoAcaoResiduoController {

    private final PlanoAcaoResiduoService service;

    public PlanoAcaoResiduoController(PlanoAcaoResiduoService service) {
        this.service = service;
    }

    @PostMapping("/nao-conformidades/{ncId}/planos-acao")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_NAO_CONFORMIDADE, action = Action.CREATE)
    public PlanoAcaoResiduoDto criar(@PathVariable Long ncId,
                                      @Validated @RequestBody PlanoAcaoResiduoRequest req) {
        return service.criar(ncId, req);
    }

    @GetMapping("/planos-acao")
    @RequiresPermission(resource = ResourceType.PGRSS_NAO_CONFORMIDADE, action = Action.READ)
    public Page<PlanoAcaoResiduoDto> search(Pageable pageable) {
        return service.search(pageable);
    }

    @PatchMapping("/planos-acao/{id}/concluir")
    @RequiresPermission(resource = ResourceType.PGRSS_NAO_CONFORMIDADE, action = Action.UPDATE)
    public PlanoAcaoResiduoDto concluir(@PathVariable Long id,
                                         @RequestBody(required = false) PlanoAcaoConcluirRequest req) {
        String evidencia = req != null ? req.descricaoEvidencia() : null;
        return service.concluir(id, evidencia);
    }

    @PatchMapping("/planos-acao/{id}/cancelar")
    @RequiresPermission(resource = ResourceType.PGRSS_NAO_CONFORMIDADE, action = Action.UPDATE)
    public PlanoAcaoResiduoDto cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
