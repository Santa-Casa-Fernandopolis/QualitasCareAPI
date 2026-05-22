package com.erp.qualitascareapi.pgrss.api;

import com.erp.qualitascareapi.pgrss.api.dto.EmpresaColetorDto;
import com.erp.qualitascareapi.pgrss.api.dto.EmpresaColetorRequest;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.CREATE)
    public EmpresaColetorDto create(@Validated @RequestBody EmpresaColetorRequest req) {
        return service.create(req);
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public Page<EmpresaColetorDto> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public EmpresaColetorDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.UPDATE)
    public EmpresaColetorDto update(@PathVariable Long id, @Validated @RequestBody EmpresaColetorRequest req) {
        return service.update(id, req);
    }

    @PatchMapping("/{id}/ativo")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.UPDATE)
    public EmpresaColetorDto toggleAtivo(@PathVariable Long id) {
        return service.toggleAtivo(id);
    }

    @GetMapping("/licencas/vencendo")
    @RequiresPermission(resource = ResourceType.PGRSS_CADASTRO, action = Action.READ)
    public List<EmpresaColetorDto> licencasVencendo(@RequestParam(defaultValue = "30") int dias) {
        return service.findLicencasProximasVencimento(dias);
    }
}
