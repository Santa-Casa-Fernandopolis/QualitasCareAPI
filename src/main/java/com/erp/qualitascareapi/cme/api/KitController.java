package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.application.KitService;
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
public class KitController {

    private final KitService kitService;

    public KitController(KitService kitService) {
        this.kitService = kitService;
    }

    @PostMapping("/instrumentos")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.CREATE, feature = "CADASTRO")
    public InstrumentoDto createInstrumento(@Validated @RequestBody InstrumentoRequest request) {
        return kitService.createInstrumento(request);
    }

    @GetMapping("/instrumentos")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.READ, feature = "LISTA")
    public Page<InstrumentoDto> listInstrumentos(Pageable pageable) {
        return kitService.listInstrumentos(pageable);
    }

    @GetMapping("/instrumentos/{id}")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.READ, feature = "LISTA")
    public InstrumentoDto getInstrumento(@PathVariable Long id) {
        return kitService.findInstrumentoById(id);
    }

    @PutMapping("/instrumentos/{id}")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.UPDATE, feature = "CADASTRO")
    public InstrumentoDto updateInstrumento(@PathVariable Long id, @Validated @RequestBody InstrumentoRequest request) {
        return kitService.updateInstrumento(id, request);
    }

    @PostMapping("/kits")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.CREATE, feature = "CADASTRO")
    public KitProcedimentoDto createKit(@Validated @RequestBody KitProcedimentoRequest request) {
        return kitService.createKitProcedimento(request);
    }

    @GetMapping("/kits")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.READ, feature = "LISTA")
    public Page<KitProcedimentoDto> listKits(Pageable pageable) {
        return kitService.listKits(pageable);
    }

    @GetMapping("/kits/{id}")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.READ, feature = "LISTA")
    public KitProcedimentoDto getKit(@PathVariable Long id) {
        return kitService.findKitById(id);
    }

    @PutMapping("/kits/{id}")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.UPDATE, feature = "CADASTRO")
    public KitProcedimentoDto updateKit(@PathVariable Long id, @Validated @RequestBody KitProcedimentoRequest request) {
        return kitService.updateKit(id, request);
    }

    @PostMapping("/kits/versoes")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.CREATE, feature = "VERSAO")
    public KitVersionDto createKitVersion(@Validated @RequestBody KitVersionRequest request) {
        return kitService.createKitVersion(request);
    }

    @GetMapping("/kits/versoes")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.READ, feature = "VERSAO")
    public Page<KitVersionDto> listKitVersions(@RequestParam(required = false) Long kitId, Pageable pageable) {
        return kitService.listKitVersions(kitId, pageable);
    }

    @DeleteMapping("/kits/versoes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.DELETE, feature = "VERSAO")
    public void deleteKitVersion(@PathVariable Long id) {
        kitService.deleteKitVersion(id);
    }

    @PostMapping("/kits/itens")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.CREATE, feature = "ITEM")
    public KitItemDto createKitItem(@Validated @RequestBody KitItemRequest request) {
        return kitService.createKitItem(request);
    }

    @GetMapping("/kits/itens")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.READ, feature = "ITEM")
    public Page<KitItemDto> listKitItems(@RequestParam(required = false) Long versaoId, Pageable pageable) {
        return kitService.listKitItems(versaoId, pageable);
    }
}
