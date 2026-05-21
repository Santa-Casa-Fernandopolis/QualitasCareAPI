package com.erp.qualitascareapi.pgrss.api;

import com.erp.qualitascareapi.pgrss.api.dto.*;
import com.erp.qualitascareapi.pgrss.application.EmpresaColetorService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pgrss/empresas-coletoras")
public class EmpresaColetorController {

    private final EmpresaColetorService service;

    public EmpresaColetorController(EmpresaColetorService service) {
        this.service = service;
    }

    // ── EmpresaColetora ────────────────────────────────────────────────────

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.CREATE)
    public EmpresaColetorDto create(@Validated @RequestBody EmpresaColetorRequest request) {
        return service.create(request);
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public Page<EmpresaColetorDto> list(@RequestParam(required = false) Boolean ativo, Pageable pageable) {
        return service.list(ativo, pageable);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public EmpresaColetorDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.UPDATE)
    public EmpresaColetorDto update(@PathVariable Long id, @Validated @RequestBody EmpresaColetorRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.UPDATE)
    public EmpresaColetorDto toggleAtivo(@PathVariable Long id, @RequestParam Boolean ativo) {
        return service.toggleAtivo(id, ativo);
    }

    // ── Custos de Tratamento ───────────────────────────────────────────────

    @PostMapping("/custos")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.CREATE)
    public CustoTratamentoDto createCusto(@Validated @RequestBody CustoTratamentoRequest request) {
        return service.createCusto(request);
    }

    @GetMapping("/{id}/custos")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public List<CustoTratamentoDto> listCustos(@PathVariable Long id) {
        return service.listCustosByEmpresa(id);
    }

    @DeleteMapping("/custos/{custoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.DELETE)
    public void deleteCusto(@PathVariable Long custoId) {
        service.deleteCusto(custoId);
    }
}
