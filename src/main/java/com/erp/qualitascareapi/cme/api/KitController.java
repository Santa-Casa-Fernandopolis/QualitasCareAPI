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

    @PatchMapping("/kits/versoes/{id}/aprovacao")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.UPDATE, feature = "VERSAO")
    public KitVersionDto aprovarKitVersion(@PathVariable Long id, @Validated @RequestBody KitVersionApprovalRequest request) {
        return kitService.registrarAprovacaoVersao(id, request);
    }

    @PatchMapping("/kits/versoes/{id}/revalidacao")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.UPDATE, feature = "VERSAO")
    public KitVersionDto revalidarKitVersion(@PathVariable Long id, @Validated @RequestBody KitVersionRevalidacaoRequest request) {
        return kitService.revalidarVersao(id, request);
    }

    @PostMapping("/kits/itens")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.CREATE, feature = "ITEM")
    public KitItemDto createKitItem(@Validated @RequestBody KitItemRequest request) {
        return kitService.createKitItem(request);
    }

    @PutMapping("/kits/itens/{id}")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.UPDATE, feature = "ITEM")
    public KitItemDto updateKitItem(@PathVariable Long id, @Validated @RequestBody KitItemRequest request) {
        return kitService.updateKitItem(id, request);
    }

    @DeleteMapping("/kits/itens/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.DELETE, feature = "ITEM")
    public void deleteKitItem(@PathVariable Long id) {
        kitService.deleteKitItem(id);
    }

    @GetMapping("/kits/itens")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.READ, feature = "ITEM")
    public Page<KitItemDto> listKitItems(@RequestParam(required = false) Long versaoId, Pageable pageable) {
        return kitService.listKitItems(versaoId, pageable);
    }

    @PostMapping("/instrumentos-fisicos")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.CREATE, feature = "CADASTRO")
    public InstrumentoFisicoDto createInstrumentoFisico(@Validated @RequestBody InstrumentoFisicoRequest request) {
        return kitService.createInstrumentoFisico(request);
    }

    @GetMapping("/instrumentos-fisicos")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.READ, feature = "LISTA")
    public Page<InstrumentoFisicoDto> listInstrumentosFisicos(Pageable pageable) {
        return kitService.listInstrumentosFisicos(pageable);
    }

    @GetMapping("/instrumentos-fisicos/{id}")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.READ, feature = "LISTA")
    public InstrumentoFisicoDto getInstrumentoFisico(@PathVariable Long id) {
        return kitService.findInstrumentoFisicoById(id);
    }

    @PutMapping("/instrumentos-fisicos/{id}")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.UPDATE, feature = "CADASTRO")
    public InstrumentoFisicoDto updateInstrumentoFisico(@PathVariable Long id, @Validated @RequestBody InstrumentoFisicoRequest request) {
        return kitService.updateInstrumentoFisico(id, request);
    }

    @PostMapping("/kits-fisicos")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.CREATE, feature = "CADASTRO")
    public KitFisicoDto createKitFisico(@Validated @RequestBody KitFisicoRequest request) {
        return kitService.createKitFisico(request);
    }

    @GetMapping("/kits-fisicos")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.READ, feature = "LISTA")
    public Page<KitFisicoDto> listKitsFisicos(Pageable pageable) {
        return kitService.listKitsFisicos(pageable);
    }

    @GetMapping("/kits-fisicos/{id}")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.READ, feature = "LISTA")
    public KitFisicoDto getKitFisico(@PathVariable Long id) {
        return kitService.findKitFisicoById(id);
    }

    @PutMapping("/kits-fisicos/{id}")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.UPDATE, feature = "CADASTRO")
    public KitFisicoDto updateKitFisico(@PathVariable Long id, @Validated @RequestBody KitFisicoRequest request) {
        return kitService.updateKitFisico(id, request);
    }

    @PostMapping("/kits-fisicos/instrumentos")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.CREATE, feature = "ITEM")
    public KitFisicoInstrumentoDto vincularInstrumentoFisico(@Validated @RequestBody KitFisicoInstrumentoRequest request) {
        return kitService.vincularInstrumentoFisico(request);
    }

    @GetMapping("/kits-fisicos/{id}/instrumentos")
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.READ, feature = "ITEM")
    public Page<KitFisicoInstrumentoDto> listInstrumentosDoKitFisico(@PathVariable Long id, Pageable pageable) {
        return kitService.listInstrumentosDoKitFisico(id, pageable);
    }

    @DeleteMapping("/kits-fisicos/instrumentos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission(resource = ResourceType.CME_KIT, action = Action.DELETE, feature = "ITEM")
    public void deleteInstrumentoKitFisico(@PathVariable Long id) {
        kitService.desvincularInstrumentoFisico(id);
    }
}
