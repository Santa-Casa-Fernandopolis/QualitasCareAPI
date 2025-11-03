package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.application.KitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cme")
public class KitController {

    private final KitService kitService;

    public KitController(KitService kitService) {
        this.kitService = kitService;
    }

    @PostMapping("/instrumentos")
    @ResponseStatus(HttpStatus.CREATED)
    public InstrumentoDto createInstrumento(@Validated @RequestBody InstrumentoRequest request) {
        return kitService.createInstrumento(request);
    }

    @GetMapping("/instrumentos")
    public Page<InstrumentoDto> listInstrumentos(Pageable pageable) {
        return kitService.listInstrumentos(pageable);
    }

    @PostMapping("/kits")
    @ResponseStatus(HttpStatus.CREATED)
    public KitProcedimentoDto createKit(@Validated @RequestBody KitProcedimentoRequest request) {
        return kitService.createKitProcedimento(request);
    }

    @GetMapping("/kits")
    public Page<KitProcedimentoDto> listKits(Pageable pageable) {
        return kitService.listKits(pageable);
    }

    @PostMapping("/kits/versoes")
    @ResponseStatus(HttpStatus.CREATED)
    public KitVersionDto createKitVersion(@Validated @RequestBody KitVersionRequest request) {
        return kitService.createKitVersion(request);
    }

    @GetMapping("/kits/versoes")
    public Page<KitVersionDto> listKitVersions(Pageable pageable) {
        return kitService.listKitVersions(pageable);
    }

    @PostMapping("/kits/itens")
    @ResponseStatus(HttpStatus.CREATED)
    public KitItemDto createKitItem(@Validated @RequestBody KitItemRequest request) {
        return kitService.createKitItem(request);
    }

    @GetMapping("/kits/itens")
    public Page<KitItemDto> listKitItems(Pageable pageable) {
        return kitService.listKitItems(pageable);
    }
}
