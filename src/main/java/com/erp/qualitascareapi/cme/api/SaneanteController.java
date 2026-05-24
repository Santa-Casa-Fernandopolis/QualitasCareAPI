package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.SaneanteLoteDto;
import com.erp.qualitascareapi.cme.api.dto.SaneanteLoteRequest;
import com.erp.qualitascareapi.cme.api.dto.UsoSaneanteDto;
import com.erp.qualitascareapi.cme.api.dto.UsoSaneanteRequest;
import com.erp.qualitascareapi.cme.application.SaneanteService;
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
public class SaneanteController {

    private final SaneanteService saneanteService;

    public SaneanteController(SaneanteService saneanteService) {
        this.saneanteService = saneanteService;
    }

    @PostMapping("/saneantes")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_SANEANTE, action = Action.CREATE, feature = "CADASTRO")
    public SaneanteLoteDto createSaneante(@Validated @RequestBody SaneanteLoteRequest request) {
        return saneanteService.createLote(request);
    }

    @GetMapping("/saneantes")
    @RequiresPermission(resource = ResourceType.CME_SANEANTE, action = Action.READ, feature = "LISTA")
    public Page<SaneanteLoteDto> listSaneantes(Pageable pageable) {
        return saneanteService.listLotes(pageable);
    }

    @GetMapping("/saneantes/{id}")
    @RequiresPermission(resource = ResourceType.CME_SANEANTE, action = Action.READ, feature = "LISTA")
    public SaneanteLoteDto getSaneante(@PathVariable Long id) {
        return saneanteService.findLoteById(id);
    }

    @PutMapping("/saneantes/{id}")
    @RequiresPermission(resource = ResourceType.CME_SANEANTE, action = Action.UPDATE, feature = "CADASTRO")
    public SaneanteLoteDto updateSaneante(@PathVariable Long id, @Validated @RequestBody SaneanteLoteRequest request) {
        return saneanteService.updateLote(id, request);
    }

    @PostMapping("/saneantes/usos")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_SANEANTE, action = Action.CREATE, feature = "FORM")
    public UsoSaneanteDto registrarUso(@Validated @RequestBody UsoSaneanteRequest request) {
        return saneanteService.registrarUso(request);
    }

    @GetMapping("/saneantes/usos")
    @RequiresPermission(resource = ResourceType.CME_SANEANTE, action = Action.READ, feature = "LISTA")
    public Page<UsoSaneanteDto> listUsos(Pageable pageable) {
        return saneanteService.listUsos(pageable);
    }
}
