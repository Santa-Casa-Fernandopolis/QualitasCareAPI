package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.application.ProcessamentoService;
import com.erp.qualitascareapi.cme.enums.RecebimentoStatus;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cme")
public class ProcessamentoController {

    private final ProcessamentoService processamentoService;

    public ProcessamentoController(ProcessamentoService processamentoService) {
        this.processamentoService = processamentoService;
    }

    // ---- Recebimento de Material ----

    @PostMapping("/recebimentos")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_PROCESSAMENTO, action = Action.CREATE)
    public RecebimentoMaterialDto registrarRecebimento(@Validated @RequestBody RecebimentoMaterialRequest request) {
        return processamentoService.registrarRecebimento(request);
    }

    @GetMapping("/recebimentos")
    @RequiresPermission(resource = ResourceType.CME_PROCESSAMENTO, action = Action.READ)
    public Page<RecebimentoMaterialDto> listRecebimentos(Pageable pageable) {
        return processamentoService.listRecebimentos(pageable);
    }

    @GetMapping("/recebimentos/{id}")
    @RequiresPermission(resource = ResourceType.CME_PROCESSAMENTO, action = Action.READ)
    public RecebimentoMaterialDto getRecebimento(@PathVariable Long id) {
        return processamentoService.findRecebimentoById(id);
    }

    @PatchMapping("/recebimentos/{id}/status")
    @RequiresPermission(resource = ResourceType.CME_PROCESSAMENTO, action = Action.UPDATE)
    public RecebimentoMaterialDto updateRecebimentoStatus(@PathVariable Long id, @RequestParam RecebimentoStatus status) {
        return processamentoService.updateRecebimentoStatus(id, status);
    }

    // ---- Ciclo Lavadora Termodesinfetadora ----

    @PostMapping("/ciclos-lavadora")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_PROCESSAMENTO, action = Action.CREATE)
    public CicloLavadoraDto registrarCicloLavadora(@Validated @RequestBody CicloLavadoraRequest request) {
        return processamentoService.registrarCicloLavadora(request);
    }

    @GetMapping("/ciclos-lavadora")
    @RequiresPermission(resource = ResourceType.CME_PROCESSAMENTO, action = Action.READ)
    public Page<CicloLavadoraDto> listCiclosLavadora(Pageable pageable) {
        return processamentoService.listCiclosLavadora(pageable);
    }

    @GetMapping("/ciclos-lavadora/{id}")
    @RequiresPermission(resource = ResourceType.CME_PROCESSAMENTO, action = Action.READ)
    public CicloLavadoraDto getCicloLavadora(@PathVariable Long id) {
        return processamentoService.findCicloLavadoraById(id);
    }

    // ---- Monitoramento Ambiental ----

    @PostMapping("/monitoramentos-ambientais")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_PROCESSAMENTO, action = Action.CREATE)
    public MonitoramentoAmbientalDto registrarMonitoramento(@Validated @RequestBody MonitoramentoAmbientalRequest request) {
        return processamentoService.registrarMonitoramento(request);
    }

    @GetMapping("/monitoramentos-ambientais")
    @RequiresPermission(resource = ResourceType.CME_PROCESSAMENTO, action = Action.READ)
    public Page<MonitoramentoAmbientalDto> listMonitoramentos(Pageable pageable) {
        return processamentoService.listMonitoramentos(pageable);
    }

    @GetMapping("/monitoramentos-ambientais/{id}")
    @RequiresPermission(resource = ResourceType.CME_PROCESSAMENTO, action = Action.READ)
    public MonitoramentoAmbientalDto getMonitoramento(@PathVariable Long id) {
        return processamentoService.findMonitoramentoById(id);
    }
}
