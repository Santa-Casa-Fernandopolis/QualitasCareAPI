package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.SaneanteLoteDto;
import com.erp.qualitascareapi.cme.api.dto.SaneanteLoteRequest;
import com.erp.qualitascareapi.cme.api.dto.UsoSaneanteDto;
import com.erp.qualitascareapi.cme.api.dto.UsoSaneanteRequest;
import com.erp.qualitascareapi.cme.application.SaneanteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cme")
public class SaneanteController {

    private final SaneanteService saneanteService;

    public SaneanteController(SaneanteService saneanteService) {
        this.saneanteService = saneanteService;
    }

    @PostMapping("/saneantes")
    @ResponseStatus(HttpStatus.CREATED)
    public SaneanteLoteDto createSaneante(@Validated @RequestBody SaneanteLoteRequest request) {
        return saneanteService.createLote(request);
    }

    @GetMapping("/saneantes")
    public Page<SaneanteLoteDto> listSaneantes(Pageable pageable) {
        return saneanteService.listLotes(pageable);
    }

    @PostMapping("/saneantes/usos")
    @ResponseStatus(HttpStatus.CREATED)
    public UsoSaneanteDto registrarUso(@Validated @RequestBody UsoSaneanteRequest request) {
        return saneanteService.registrarUso(request);
    }

    @GetMapping("/saneantes/usos")
    public Page<UsoSaneanteDto> listUsos(Pageable pageable) {
        return saneanteService.listUsos(pageable);
    }
}
