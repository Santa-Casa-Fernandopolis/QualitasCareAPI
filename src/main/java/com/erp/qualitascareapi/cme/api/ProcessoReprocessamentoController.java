package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.application.ProcessoReprocessamentoService;
import com.erp.qualitascareapi.cme.enums.ProcessoStatus;
import org.springframework.http.HttpStatus;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/cme")
public class ProcessoReprocessamentoController {

    private final ProcessoReprocessamentoService service;

    public ProcessoReprocessamentoController(ProcessoReprocessamentoService service) {
        this.service = service;
    }

    @PostMapping("/processos")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.CREATE)
    public ResponseEntity<ProcessoReprocessamentoDto> createProcesso(
            @Valid @RequestBody ProcessoReprocessamentoRequest request) {
        ProcessoReprocessamentoDto dto = service.createProcesso(request);
        return ResponseEntity.created(URI.create("/api/cme/processos/" + dto.id())).body(dto);
    }

    @GetMapping("/processos")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ)
    public Page<ProcessoReprocessamentoDto> listProcessos(Pageable pageable) {
        return service.listProcessos(pageable);
    }

    @GetMapping("/processos/{id}")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ)
    public ProcessoReprocessamentoDto findProcessoById(@PathVariable Long id) {
        return service.findProcessoById(id);
    }

    @PatchMapping("/processos/{id}/status")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.UPDATE)
    public ProcessoReprocessamentoDto updateProcessoStatus(
            @PathVariable Long id, @RequestParam ProcessoStatus status) {
        return service.updateProcessoStatus(id, status);
    }

    @GetMapping("/processos/{id}/timeline")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ)
    public ProcessoTimelineDto getTimeline(@PathVariable Long id) {
        return service.getTimeline(id);
    }

    @PostMapping("/limpezas-manuais")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.CREATE)
    public ResponseEntity<LimpezaManualDto> registrarLimpeza(
            @Valid @RequestBody LimpezaManualRequest request) {
        LimpezaManualDto dto = service.registrarLimpezaManual(request);
        return ResponseEntity.created(URI.create("/api/cme/limpezas-manuais/" + dto.id())).body(dto);
    }

    @GetMapping("/limpezas-manuais")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ)
    public Page<LimpezaManualDto> listLimpezas(Pageable pageable) {
        return service.listLimpezas(pageable);
    }

    @GetMapping("/limpezas-manuais/{id}")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ)
    public LimpezaManualDto findLimpezaById(@PathVariable Long id) {
        return service.findLimpezaById(id);
    }

    // ---- Secagem de Material ----

    @PostMapping("/secagens")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.CREATE)
    public SecagemMaterialDto registrarSecagem(@Valid @RequestBody SecagemMaterialRequest request) {
        return service.registrarSecagem(request);
    }

    @GetMapping("/secagens")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ)
    public Page<SecagemMaterialDto> listSecagens(Pageable pageable) {
        return service.listSecagens(pageable);
    }

    // ---- Conferência de Kit ----

    @PostMapping("/conferencias-kit")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.CREATE)
    public ConferenciaKitDto registrarConferencia(@Valid @RequestBody ConferenciaKitRequest request) {
        return service.registrarConferencia(request);
    }

    @GetMapping("/conferencias-kit")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ)
    public Page<ConferenciaKitDto> listConferencias(Pageable pageable) {
        return service.listConferencias(pageable);
    }
}
