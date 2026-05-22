package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.application.CmeAprovacaoService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cme")
public class AprovacaoCmeController {

    private final CmeAprovacaoService service;

    public AprovacaoCmeController(CmeAprovacaoService service) {
        this.service = service;
    }

    // ---- Definições de fluxo de aprovação CME ----

    @GetMapping("/approval-flow-defs")
    @RequiresPermission(resource = ResourceType.CME_QUALIDADE, action = Action.READ)
    public List<CmeFlowDefDto> listFlowDefs() {
        return service.listFlowDefs();
    }

    @PostMapping("/approval-flow-defs")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_QUALIDADE, action = Action.CREATE)
    public CmeFlowDefDto createFlowDef(@Valid @RequestBody CmeFlowDefRequest request) {
        return service.createFlowDef(request);
    }

    // ---- Aprovações de Ciclo de Esterilização ----

    @GetMapping("/aprovacoes/ciclos")
    @RequiresPermission(resource = ResourceType.CME_QUALIDADE, action = Action.READ)
    public Page<AprovacaoCicloDto> listAprovacoesCiclo(Pageable pageable) {
        return service.listAprovacoesCiclo(pageable);
    }

    @PostMapping("/aprovacoes/ciclos")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_QUALIDADE, action = Action.CREATE)
    public AprovacaoCicloDto registrarAprovacaoCiclo(@Valid @RequestBody AprovacaoCicloRequest request) {
        return service.registrarAprovacaoCiclo(request);
    }

    // ---- Aprovações de Lote Inalatório ----

    @GetMapping("/aprovacoes/lotes-inalatorios")
    @RequiresPermission(resource = ResourceType.CME_QUALIDADE, action = Action.READ)
    public Page<AprovacaoLoteInalatorioDto> listAprovacoesCicloLote(Pageable pageable) {
        return service.listAprovacoesCicloLote(pageable);
    }

    @PostMapping("/aprovacoes/lotes-inalatorios")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_QUALIDADE, action = Action.CREATE)
    public AprovacaoLoteInalatorioDto registrarAprovacaoLote(@Valid @RequestBody AprovacaoLoteInalatorioRequest request) {
        return service.registrarAprovacaoLote(request);
    }
}
