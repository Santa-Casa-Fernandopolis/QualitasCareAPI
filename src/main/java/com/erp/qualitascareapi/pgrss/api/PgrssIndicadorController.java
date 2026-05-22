package com.erp.qualitascareapi.pgrss.api;

import com.erp.qualitascareapi.pgrss.api.dto.MesPesoDto;
import com.erp.qualitascareapi.pgrss.api.dto.PgrssDashboardDto;
import com.erp.qualitascareapi.pgrss.api.dto.SetorPesoDto;
import com.erp.qualitascareapi.pgrss.application.IndicadorResiduoService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pgrss/indicadores")
public class PgrssIndicadorController {

    private final IndicadorResiduoService service;

    public PgrssIndicadorController(IndicadorResiduoService service) {
        this.service = service;
    }

    @GetMapping("/peso-total")
    @RequiresPermission(resource = ResourceType.PGRSS_INDICADOR, action = Action.READ)
    public BigDecimal pesoTotal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return service.pesoTotalPorPeriodo(inicio, fim);
    }

    @GetMapping("/peso-por-setor")
    @RequiresPermission(resource = ResourceType.PGRSS_INDICADOR, action = Action.READ)
    public Map<String, BigDecimal> pesoPorSetor(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return service.pesoPorSetor(inicio, fim);
    }

    @GetMapping("/peso-por-grupo")
    @RequiresPermission(resource = ResourceType.PGRSS_INDICADOR, action = Action.READ)
    public Map<String, BigDecimal> pesoPorGrupo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return service.pesoPorGrupo(inicio, fim);
    }

    @GetMapping("/taxa-infectante")
    @RequiresPermission(resource = ResourceType.PGRSS_INDICADOR, action = Action.READ)
    public double taxaInfectante(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return service.taxaResiduoInfectante(inicio, fim);
    }

    @GetMapping("/nao-conformidades-por-setor")
    @RequiresPermission(resource = ResourceType.PGRSS_INDICADOR, action = Action.READ)
    public Map<String, Long> naoConformidadesPorSetor(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return service.naoConformidadesPorSetor(inicio, fim);
    }

    @GetMapping("/nao-conformidades-por-tipo")
    @RequiresPermission(resource = ResourceType.PGRSS_INDICADOR, action = Action.READ)
    public Map<String, Long> naoConformidadesPorTipo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return service.naoConformidadesPorTipo(inicio, fim);
    }

    @GetMapping("/custo-por-grupo")
    @RequiresPermission(resource = ResourceType.PGRSS_INDICADOR, action = Action.READ)
    public Map<String, BigDecimal> custoPorGrupo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return service.custoEstimadoPorGrupo(inicio, fim);
    }

    @GetMapping("/dashboard")
    @RequiresPermission(resource = ResourceType.PGRSS_INDICADOR, action = Action.READ)
    public PgrssDashboardDto dashboard() {
        return service.getDashboard();
    }
}
