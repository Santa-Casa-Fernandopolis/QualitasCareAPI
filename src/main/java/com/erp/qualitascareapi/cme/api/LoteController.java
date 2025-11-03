package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.application.LoteService;
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
    public SetorDto createSetor(@Validated @RequestBody SetorRequest request) {
        return loteService.createSetor(request);
    }

    @GetMapping("/setores")
    public Page<SetorDto> listSetores(Pageable pageable) {
        return loteService.listSetores(pageable);
    }

    @PostMapping("/lotes")
    @ResponseStatus(HttpStatus.CREATED)
    public LoteEtiquetaDto createLote(@Validated @RequestBody LoteEtiquetaRequest request) {
        return loteService.createLote(request);
    }

    @GetMapping("/lotes")
    public Page<LoteEtiquetaDto> listLotes(Pageable pageable) {
        return loteService.listLotes(pageable);
    }

    @PostMapping("/movimentacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public MovimentacaoDto registrarMovimentacao(@Validated @RequestBody MovimentacaoRequest request) {
        return loteService.registrarMovimentacao(request);
    }

    @GetMapping("/movimentacoes")
    public Page<MovimentacaoDto> listMovimentacoes(Pageable pageable) {
        return loteService.listMovimentacoes(pageable);
    }
}
