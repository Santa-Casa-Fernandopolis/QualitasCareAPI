package com.erp.qualitascareapi.environmental.api;

import com.erp.qualitascareapi.environmental.api.dto.*;
import com.erp.qualitascareapi.environmental.application.EnvironmentalService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/env")
public class EnvironmentalController {

    private final EnvironmentalService environmentalService;

    public EnvironmentalController(EnvironmentalService environmentalService) {
        this.environmentalService = environmentalService;
    }

    // ---- Dashboard ----

    @GetMapping("/dashboard")
    @RequiresPermission(resource = ResourceType.ENV_MONITORAMENTO, action = Action.READ)
    public EnvironmentalDashboardDto getDashboard() {
        return environmentalService.getDashboard();
    }

    // ---- Monitoramento Ambiental (temperatura, umidade, pressão diferencial) ----

    @PostMapping("/monitoramentos-ambientais")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.ENV_MONITORAMENTO, action = Action.CREATE)
    public MonitoramentoAmbientalDto registrarMonitoramento(
            @Validated @RequestBody MonitoramentoAmbientalRequest request) {
        return environmentalService.registrarMonitoramento(request);
    }

    @GetMapping("/monitoramentos-ambientais")
    @RequiresPermission(resource = ResourceType.ENV_MONITORAMENTO, action = Action.READ)
    public Page<MonitoramentoAmbientalDto> listMonitoramentos(Pageable pageable) {
        return environmentalService.listMonitoramentos(pageable);
    }

    @GetMapping("/monitoramentos-ambientais/{id}")
    @RequiresPermission(resource = ResourceType.ENV_MONITORAMENTO, action = Action.READ)
    public MonitoramentoAmbientalDto getMonitoramento(@PathVariable Long id) {
        return environmentalService.findMonitoramentoById(id);
    }

    // ---- Geladeiras de Medicamentos / Vacinas ----

    @PostMapping("/geladeiras")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.ENV_GELADEIRA, action = Action.CREATE)
    public GeladeiraMedicamentosDto cadastrarGeladeira(
            @Validated @RequestBody GeladeiraMedicamentosRequest request) {
        return environmentalService.cadastrarGeladeira(request);
    }

    @PutMapping("/geladeiras/{id}")
    @RequiresPermission(resource = ResourceType.ENV_GELADEIRA, action = Action.UPDATE)
    public GeladeiraMedicamentosDto updateGeladeira(
            @PathVariable Long id,
            @Validated @RequestBody GeladeiraMedicamentosRequest request) {
        return environmentalService.updateGeladeira(id, request);
    }

    @PatchMapping("/geladeiras/{id}/status")
    @RequiresPermission(resource = ResourceType.ENV_GELADEIRA, action = Action.UPDATE)
    public GeladeiraMedicamentosDto toggleGeladeiraStatus(
            @PathVariable Long id,
            @RequestParam boolean ativo) {
        return environmentalService.toggleGeladeiraStatus(id, ativo);
    }

    @GetMapping("/geladeiras")
    @RequiresPermission(resource = ResourceType.ENV_GELADEIRA, action = Action.READ)
    public Page<GeladeiraMedicamentosDto> listGeladeiras(Pageable pageable) {
        return environmentalService.listGeladeiras(pageable);
    }

    @GetMapping("/geladeiras/{id}")
    @RequiresPermission(resource = ResourceType.ENV_GELADEIRA, action = Action.READ)
    public GeladeiraMedicamentosDto getGeladeira(@PathVariable Long id) {
        return environmentalService.findGeladeiraById(id);
    }

    // ---- Registros de Temperatura de Geladeira ----

    @PostMapping("/geladeiras/registros-temperatura")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.ENV_GELADEIRA, action = Action.CREATE)
    public RegistroTemperaturaGeladeiraDto registrarTemperatura(
            @Validated @RequestBody RegistroTemperaturaGeladeiraRequest request) {
        return environmentalService.registrarTemperatura(request);
    }

    @GetMapping("/geladeiras/registros-temperatura")
    @RequiresPermission(resource = ResourceType.ENV_GELADEIRA, action = Action.READ)
    public Page<RegistroTemperaturaGeladeiraDto> listRegistros(
            @RequestParam(required = false) Long geladeiraId,
            Pageable pageable) {
        return environmentalService.listRegistros(geladeiraId, pageable);
    }

    @GetMapping("/geladeiras/registros-temperatura/{id}")
    @RequiresPermission(resource = ResourceType.ENV_GELADEIRA, action = Action.READ)
    public RegistroTemperaturaGeladeiraDto getRegistro(@PathVariable Long id) {
        return environmentalService.findRegistroById(id);
    }
}
