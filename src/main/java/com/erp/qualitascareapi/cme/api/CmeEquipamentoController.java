package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.CmeEquipamentoDto;
import com.erp.qualitascareapi.cme.api.dto.CmeEquipamentoRequest;
import com.erp.qualitascareapi.cme.application.CmeEquipamentoService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cme/equipamentos")
public class CmeEquipamentoController {
    private final CmeEquipamentoService service;

    public CmeEquipamentoController(CmeEquipamentoService service) {
        this.service = service;
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.READ, feature = "LISTA")
    public List<CmeEquipamentoDto> list() {
        return service.list();
    }

    @PostMapping
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.CREATE, feature = "FORM")
    public ResponseEntity<CmeEquipamentoDto> create(@Valid @RequestBody CmeEquipamentoRequest request) {
        CmeEquipamentoDto dto = service.create(request);
        return ResponseEntity.created(URI.create("/api/cme/equipamentos/" + dto.id())).body(dto);
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = ResourceType.CME_PROCESSO_REPROCESSAMENTO, action = Action.UPDATE, feature = "FORM")
    public CmeEquipamentoDto update(@PathVariable Long id, @Valid @RequestBody CmeEquipamentoRequest request) {
        return service.update(id, request);
    }
}
