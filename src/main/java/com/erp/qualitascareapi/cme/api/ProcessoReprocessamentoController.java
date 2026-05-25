package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.application.ProcessoReprocessamentoService;
import com.erp.qualitascareapi.cme.enums.ProcessoStatus;
import com.erp.qualitascareapi.cme.enums.TipoFluxoCME;
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
import java.util.List;

@RestController
@RequestMapping("/api/cme")
public class ProcessoReprocessamentoController {

    private final ProcessoReprocessamentoService service;

    public ProcessoReprocessamentoController(ProcessoReprocessamentoService service) {
        this.service = service;
    }

    @PostMapping("/processos")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.CREATE, feature = "FORM")
    public ResponseEntity<ProcessoReprocessamentoDto> createProcesso(
            @Valid @RequestBody ProcessoReprocessamentoRequest request) {
        ProcessoReprocessamentoDto dto = service.createProcesso(request);
        return ResponseEntity.created(URI.create("/api/cme/processos/" + dto.id())).body(dto);
    }

    @GetMapping("/processos")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ, feature = "LISTA")
    public Page<ProcessoReprocessamentoDto> listProcessos(Pageable pageable) {
        return service.listProcessos(pageable);
    }

    @GetMapping("/processos/{id}")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ, feature = "LISTA")
    public ProcessoReprocessamentoDto findProcessoById(@PathVariable Long id) {
        return service.findProcessoById(id);
    }

    @PatchMapping("/processos/{id}/status")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.UPDATE, feature = "FORM")
    public ProcessoReprocessamentoDto updateProcessoStatus(
            @PathVariable Long id, @RequestParam ProcessoStatus status) {
        return service.updateProcessoStatus(id, status);
    }

    @GetMapping("/processos/{id}/timeline")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ, feature = "LISTA")
    public ProcessoTimelineDto getTimeline(@PathVariable Long id) {
        return service.getTimeline(id);
    }

    @GetMapping("/etapas-processo")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ, feature = "LISTA")
    public List<CmeEtapaCatalogoDto> listEtapasCatalogo() {
        return service.listEtapasCatalogo();
    }

    @PostMapping("/etapas-processo")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.CREATE, feature = "FORM")
    public ResponseEntity<CmeEtapaCatalogoDto> createEtapaCatalogo(@Valid @RequestBody CmeEtapaCatalogoRequest request) {
        CmeEtapaCatalogoDto dto = service.createEtapaCatalogo(request);
        return ResponseEntity.created(URI.create("/api/cme/etapas-processo/" + dto.id())).body(dto);
    }

    @PutMapping("/etapas-processo/{id}")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.UPDATE, feature = "FORM")
    public CmeEtapaCatalogoDto updateEtapaCatalogo(
            @PathVariable Long id,
            @Valid @RequestBody CmeEtapaCatalogoRequest request) {
        return service.updateEtapaCatalogo(id, request);
    }

    @DeleteMapping("/etapas-processo/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.DELETE, feature = "FORM")
    public void deleteEtapaCatalogo(@PathVariable Long id) {
        service.deleteEtapaCatalogo(id);
    }

    @GetMapping("/fluxos-processo")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ, feature = "LISTA")
    public List<CmeFluxoProcessoDto> listFluxosProcesso() {
        return service.listFluxosProcesso();
    }

    @PostMapping("/fluxos-processo")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.CREATE, feature = "FORM")
    public ResponseEntity<CmeFluxoProcessoDto> createFluxoProcesso(@Valid @RequestBody CmeFluxoProcessoRequest request) {
        CmeFluxoProcessoDto dto = service.createFluxoProcesso(request);
        return ResponseEntity.created(URI.create("/api/cme/fluxos-processo/" + dto.id())).body(dto);
    }

    @GetMapping("/fluxos-processo/{fluxoId}/etapas")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ, feature = "LISTA")
    public List<CmeEtapaProcessoDto> listEtapasFluxo(@PathVariable Long fluxoId) {
        return service.listEtapasFluxo(fluxoId);
    }

    @PostMapping("/fluxos-processo/{fluxoId}/etapas")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.CREATE, feature = "FORM")
    public ResponseEntity<CmeEtapaProcessoDto> createEtapaFluxo(
            @PathVariable Long fluxoId,
            @Valid @RequestBody CmeEtapaProcessoRequest request) {
        CmeEtapaProcessoDto dto = service.createEtapaFluxo(fluxoId, request);
        return ResponseEntity.created(URI.create("/api/cme/fluxos-processo/" + fluxoId + "/etapas/" + dto.id())).body(dto);
    }

    @PutMapping("/fluxos-processo/{fluxoId}/etapas/{etapaId}")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.UPDATE, feature = "FORM")
    public CmeEtapaProcessoDto updateEtapaFluxo(
            @PathVariable Long fluxoId,
            @PathVariable Long etapaId,
            @Valid @RequestBody CmeEtapaProcessoRequest request) {
        return service.updateEtapaFluxo(fluxoId, etapaId, request);
    }

    @DeleteMapping("/fluxos-processo/{fluxoId}/etapas/{etapaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.DELETE, feature = "FORM")
    public void deleteEtapaFluxo(@PathVariable Long fluxoId, @PathVariable Long etapaId) {
        service.deleteEtapaFluxo(fluxoId, etapaId);
    }

    @GetMapping("/rastreabilidade")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ, feature = "LISTA")
    public List<CmeRastreabilidadeColunaDto> getRastreabilidade(
            @RequestParam(required = false) TipoFluxoCME tipoFluxo) {
        return service.getRastreabilidade(tipoFluxo);
    }

    @PostMapping("/limpezas-manuais")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.CREATE, feature = "FORM")
    public ResponseEntity<LimpezaManualDto> registrarLimpeza(
            @Valid @RequestBody LimpezaManualRequest request) {
        LimpezaManualDto dto = service.registrarLimpezaManual(request);
        return ResponseEntity.created(URI.create("/api/cme/limpezas-manuais/" + dto.id())).body(dto);
    }

    @GetMapping("/limpezas-manuais")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ, feature = "LISTA")
    public Page<LimpezaManualDto> listLimpezas(Pageable pageable) {
        return service.listLimpezas(pageable);
    }

    @GetMapping("/limpezas-manuais/{id}")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ, feature = "LISTA")
    public LimpezaManualDto findLimpezaById(@PathVariable Long id) {
        return service.findLimpezaById(id);
    }

    // ---- Secagem de Material ----

    @PostMapping("/secagens")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.CREATE, feature = "FORM")
    public SecagemMaterialDto registrarSecagem(@Valid @RequestBody SecagemMaterialRequest request) {
        return service.registrarSecagem(request);
    }

    @GetMapping("/secagens")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ, feature = "LISTA")
    public Page<SecagemMaterialDto> listSecagens(Pageable pageable) {
        return service.listSecagens(pageable);
    }

    // ---- Conferência de Kit ----

    @PostMapping("/conferencias-kit")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.CREATE, feature = "FORM")
    public ConferenciaKitDto registrarConferencia(@Valid @RequestBody ConferenciaKitRequest request) {
        return service.registrarConferencia(request);
    }

    @GetMapping("/conferencias-kit")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ, feature = "LISTA")
    public Page<ConferenciaKitDto> listConferencias(Pageable pageable) {
        return service.listConferencias(pageable);
    }
}
