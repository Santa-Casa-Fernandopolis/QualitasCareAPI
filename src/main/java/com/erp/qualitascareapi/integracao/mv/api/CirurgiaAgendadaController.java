package com.erp.qualitascareapi.integracao.mv.api;

import com.erp.qualitascareapi.integracao.mv.api.dto.CirurgiaAgendadaDto;
import com.erp.qualitascareapi.integracao.mv.api.dto.SincronizacaoResultadoDto;
import com.erp.qualitascareapi.integracao.mv.application.MvIntegracaoService;
import com.erp.qualitascareapi.integracao.mv.enums.StatusCirurgiaMv;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * API de cirurgias agendadas importadas do Soul MV.
 *
 * <pre>
 * GET  /api/mv/cirurgias               — lista paginada (filtros: data, status, tenantId)
 * GET  /api/mv/cirurgias/{id}          — detalhe
 * POST /api/mv/cirurgias/sincronizar   — dispara sync manual imediato
 * </pre>
 */
@RestController
@RequestMapping("/api/mv/cirurgias")
public class CirurgiaAgendadaController {

    private final MvIntegracaoService mvIntegracaoService;

    public CirurgiaAgendadaController(MvIntegracaoService mvIntegracaoService) {
        this.mvIntegracaoService = mvIntegracaoService;
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.CME_CIRURGIA_AGENDADA, action = Action.READ)
    public Page<CirurgiaAgendadaDto> listar(
            @RequestParam Long tenantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(required = false) StatusCirurgiaMv status,
            Pageable pageable) {
        return mvIntegracaoService.listar(tenantId, data, status, pageable);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.CME_CIRURGIA_AGENDADA, action = Action.READ)
    public CirurgiaAgendadaDto findById(
            @PathVariable Long id,
            @RequestParam Long tenantId) {
        return mvIntegracaoService.findById(tenantId, id);
    }

    /**
     * Dispara a sincronização com o MV imediatamente, independente do intervalo agendado.
     * Útil para testar a integração ou forçar atualização antes de uma cirurgia urgente.
     */
    @PostMapping("/sincronizar")
    @RequiresPermission(resource = ResourceType.CME_CIRURGIA_AGENDADA, action = Action.UPDATE)
    public SincronizacaoResultadoDto sincronizarAgora() {
        return mvIntegracaoService.sincronizarCirurgias();
    }
}
