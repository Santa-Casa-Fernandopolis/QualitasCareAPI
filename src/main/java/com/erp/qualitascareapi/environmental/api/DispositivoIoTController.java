package com.erp.qualitascareapi.environmental.api;

import com.erp.qualitascareapi.environmental.api.dto.DispositivoIoTDto;
import com.erp.qualitascareapi.environmental.api.dto.DispositivoIoTRequest;
import com.erp.qualitascareapi.environmental.application.EnvironmentalService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/env/dispositivos")
public class DispositivoIoTController {

    private final EnvironmentalService environmentalService;

    public DispositivoIoTController(EnvironmentalService environmentalService) {
        this.environmentalService = environmentalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.ENV_IOT_DEVICE, action = Action.CREATE)
    public DispositivoIoTDto cadastrarDispositivo(@Validated @RequestBody DispositivoIoTRequest request) {
        return environmentalService.cadastrarDispositivo(request);
    }

    @PatchMapping("/{id}/status")
    @RequiresPermission(resource = ResourceType.ENV_IOT_DEVICE, action = Action.UPDATE)
    public DispositivoIoTDto toggleStatus(@PathVariable Long id, @RequestParam boolean ativo) {
        return environmentalService.toggleDispositivoStatus(id, ativo);
    }

    /**
     * Regenera a chave de API do dispositivo.
     * Use quando a chave antiga for comprometida ou o dispositivo for substituído.
     */
    @PostMapping("/{id}/regenerar-chave")
    @RequiresPermission(resource = ResourceType.ENV_IOT_DEVICE, action = Action.UPDATE)
    public DispositivoIoTDto regenerarApiKey(@PathVariable Long id) {
        return environmentalService.regenerarApiKey(id);
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.ENV_IOT_DEVICE, action = Action.READ)
    public Page<DispositivoIoTDto> listDispositivos(Pageable pageable) {
        return environmentalService.listDispositivos(pageable);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.ENV_IOT_DEVICE, action = Action.READ)
    public DispositivoIoTDto getDispositivo(@PathVariable Long id) {
        return environmentalService.findDispositivoById(id);
    }
}
