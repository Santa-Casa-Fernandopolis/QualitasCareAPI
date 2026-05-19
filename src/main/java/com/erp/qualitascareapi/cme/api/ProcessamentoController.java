package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.application.ProcessamentoService;
import com.erp.qualitascareapi.cme.enums.RecebimentoStatus;
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
    public RecebimentoMaterialDto registrarRecebimento(@Validated @RequestBody RecebimentoMaterialRequest request) {
        return processamentoService.registrarRecebimento(request);
    }

    @GetMapping("/recebimentos")
    public Page<RecebimentoMaterialDto> listRecebimentos(Pageable pageable) {
        return processamentoService.listRecebimentos(pageable);
    }

    @GetMapping("/recebimentos/{id}")
    public RecebimentoMaterialDto getRecebimento(@PathVariable Long id) {
        return processamentoService.findRecebimentoById(id);
    }

    @PatchMapping("/recebimentos/{id}/status")
    public RecebimentoMaterialDto updateRecebimentoStatus(@PathVariable Long id, @RequestParam RecebimentoStatus status) {
        return processamentoService.updateRecebimentoStatus(id, status);
    }

    // ---- Ciclo Lavadora Termodesinfetadora ----

    @PostMapping("/ciclos-lavadora")
    @ResponseStatus(HttpStatus.CREATED)
    public CicloLavadoraDto registrarCicloLavadora(@Validated @RequestBody CicloLavadoraRequest request) {
        return processamentoService.registrarCicloLavadora(request);
    }

    @GetMapping("/ciclos-lavadora")
    public Page<CicloLavadoraDto> listCiclosLavadora(Pageable pageable) {
        return processamentoService.listCiclosLavadora(pageable);
    }

    @GetMapping("/ciclos-lavadora/{id}")
    public CicloLavadoraDto getCicloLavadora(@PathVariable Long id) {
        return processamentoService.findCicloLavadoraById(id);
    }

    // ---- Monitoramento Ambiental ----

    @PostMapping("/monitoramentos-ambientais")
    @ResponseStatus(HttpStatus.CREATED)
    public MonitoramentoAmbientalDto registrarMonitoramento(@Validated @RequestBody MonitoramentoAmbientalRequest request) {
        return processamentoService.registrarMonitoramento(request);
    }

    @GetMapping("/monitoramentos-ambientais")
    public Page<MonitoramentoAmbientalDto> listMonitoramentos(Pageable pageable) {
        return processamentoService.listMonitoramentos(pageable);
    }

    @GetMapping("/monitoramentos-ambientais/{id}")
    public MonitoramentoAmbientalDto getMonitoramento(@PathVariable Long id) {
        return processamentoService.findMonitoramentoById(id);
    }
}
