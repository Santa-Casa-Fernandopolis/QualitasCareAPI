package com.erp.qualitascareapi.pgrss.api;

import com.erp.qualitascareapi.pgrss.api.dto.CustoTratamentoDto;
import com.erp.qualitascareapi.pgrss.api.dto.CustoTratamentoRequest;
import com.erp.qualitascareapi.pgrss.application.CustoTratamentoService;
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
@RequestMapping("/api/pgrss/custos")
public class CustoTratamentoController {

    private final CustoTratamentoService service;

    public CustoTratamentoController(CustoTratamentoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.PGRSS_INDICADOR, action = Action.CREATE)
    public CustoTratamentoDto create(@Validated @RequestBody CustoTratamentoRequest req) {
        return service.create(req);
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.PGRSS_INDICADOR, action = Action.READ)
    public Page<CustoTratamentoDto> listAll(Pageable pageable) {
        return service.listAll(pageable);
    }

    @GetMapping("/por-grupo/{grupoId}")
    @RequiresPermission(resource = ResourceType.PGRSS_INDICADOR, action = Action.READ)
    public List<CustoTratamentoDto> findPorGrupo(@PathVariable Long grupoId) {
        return service.findVigenteByGrupo(grupoId);
    }
}
