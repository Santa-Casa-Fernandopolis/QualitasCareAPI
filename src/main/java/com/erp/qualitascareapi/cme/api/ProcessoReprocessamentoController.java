package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.application.ProcessoReprocessamentoService;
import com.erp.qualitascareapi.cme.enums.ProcessoStatus;
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
    public ResponseEntity<ProcessoReprocessamentoDto> createProcesso(
            @Valid @RequestBody ProcessoReprocessamentoRequest request) {
        ProcessoReprocessamentoDto dto = service.createProcesso(request);
        return ResponseEntity.created(URI.create("/api/cme/processos/" + dto.id())).body(dto);
    }

    @GetMapping("/processos")
    public Page<ProcessoReprocessamentoDto> listProcessos(Pageable pageable) {
        return service.listProcessos(pageable);
    }

    @GetMapping("/processos/{id}")
    public ProcessoReprocessamentoDto findProcessoById(@PathVariable Long id) {
        return service.findProcessoById(id);
    }

    @PatchMapping("/processos/{id}/status")
    public ProcessoReprocessamentoDto updateProcessoStatus(
            @PathVariable Long id, @RequestParam ProcessoStatus status) {
        return service.updateProcessoStatus(id, status);
    }

    @GetMapping("/processos/{id}/timeline")
    public ProcessoTimelineDto getTimeline(@PathVariable Long id) {
        return service.getTimeline(id);
    }

    @PostMapping("/limpezas-manuais")
    public ResponseEntity<LimpezaManualDto> registrarLimpeza(
            @Valid @RequestBody LimpezaManualRequest request) {
        LimpezaManualDto dto = service.registrarLimpezaManual(request);
        return ResponseEntity.created(URI.create("/api/cme/limpezas-manuais/" + dto.id())).body(dto);
    }

    @GetMapping("/limpezas-manuais")
    public Page<LimpezaManualDto> listLimpezas(Pageable pageable) {
        return service.listLimpezas(pageable);
    }

    @GetMapping("/limpezas-manuais/{id}")
    public LimpezaManualDto findLimpezaById(@PathVariable Long id) {
        return service.findLimpezaById(id);
    }
}
