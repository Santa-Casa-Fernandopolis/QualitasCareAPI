package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.application.LoteService;
import com.erp.qualitascareapi.cme.enums.LoteStatus;
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
public class LoteController {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    @PostMapping("/setores")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_LOTE, action = Action.CREATE)
    public SetorDto createSetor(@Validated @RequestBody SetorRequest request) {
        return loteService.createSetor(request);
    }

    @GetMapping("/setores")
    @RequiresPermission(resource = ResourceType.CME_LOTE, action = Action.READ)
    public Page<SetorDto> listSetores(Pageable pageable) {
        return loteService.listSetores(pageable);
    }

    @GetMapping("/setores/{id}")
    @RequiresPermission(resource = ResourceType.CME_LOTE, action = Action.READ)
    public SetorDto getSetor(@PathVariable Long id) {
        return loteService.findSetorById(id);
    }

    @PostMapping("/lotes")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_LOTE, action = Action.CREATE)
    public LoteEtiquetaDto createLote(@Validated @RequestBody LoteEtiquetaRequest request) {
        return loteService.createLote(request);
    }

    @GetMapping("/lotes")
    @RequiresPermission(resource = ResourceType.CME_LOTE, action = Action.READ)
    public Page<LoteEtiquetaDto> listLotes(Pageable pageable) {
        return loteService.listLotes(pageable);
    }

    @GetMapping("/lotes/{id}")
    @RequiresPermission(resource = ResourceType.CME_LOTE, action = Action.READ)
    public LoteEtiquetaDto getLote(@PathVariable Long id) {
        return loteService.findLoteById(id);
    }

    @GetMapping("/lotes/{id}/detalhe")
    @RequiresPermission(resource = ResourceType.CME_LOTE, action = Action.READ)
    public LoteDetalheDto getLoteDetalhe(@PathVariable Long id) {
        return loteService.findLoteDetalhe(id);
    }

    @PatchMapping("/lotes/{id}/status")
    @RequiresPermission(resource = ResourceType.CME_LOTE, action = Action.UPDATE)
    public LoteEtiquetaDto updateLoteStatus(@PathVariable Long id, @RequestParam LoteStatus status) {
        return loteService.updateLoteStatus(id, status);
    }

    @PostMapping("/lotes/baixa-uso")
    @RequiresPermission(resource = ResourceType.CME_LOTE, action = Action.UPDATE)
    public BaixaUsoLoteDto registrarBaixaUso(@Validated @RequestBody BaixaUsoLoteRequest request) {
        return loteService.registrarBaixaUso(request);
    }

    @PostMapping("/movimentacoes")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_LOTE, action = Action.CREATE)
    public MovimentacaoDto registrarMovimentacao(@Validated @RequestBody MovimentacaoRequest request) {
        return loteService.registrarMovimentacao(request);
    }

    @GetMapping("/movimentacoes")
    @RequiresPermission(resource = ResourceType.CME_LOTE, action = Action.READ)
    public Page<MovimentacaoDto> listMovimentacoes(Pageable pageable) {
        return loteService.listMovimentacoes(pageable);
    }
}
