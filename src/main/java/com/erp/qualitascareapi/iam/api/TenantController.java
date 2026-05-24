package com.erp.qualitascareapi.iam.api;

import com.erp.qualitascareapi.iam.api.dto.TenantDto;
import com.erp.qualitascareapi.iam.api.dto.TenantRequest;
import com.erp.qualitascareapi.iam.application.TenantService;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @RequiresPermission(resource = ResourceType.IAM_TENANT, action = Action.READ)
    @GetMapping
    public Page<TenantDto> list(Pageable pageable) {
        return tenantService.list(pageable);
    }

    @RequiresPermission(resource = ResourceType.IAM_TENANT, action = Action.READ)
    @GetMapping("/{id}")
    public TenantDto get(@PathVariable Long id) {
        return tenantService.get(id);
    }

    @RequiresPermission(resource = ResourceType.IAM_TENANT, action = Action.CREATE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TenantDto create(@Validated @RequestBody TenantRequest request) {
        return tenantService.create(request);
    }

    @RequiresPermission(resource = ResourceType.IAM_TENANT, action = Action.UPDATE)
    @PutMapping("/{id}")
    public TenantDto update(@PathVariable Long id, @Validated @RequestBody TenantRequest request) {
        return tenantService.update(id, request);
    }

    @RequiresPermission(resource = ResourceType.IAM_TENANT, action = Action.UPDATE)
    @PostMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TenantDto uploadLogo(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        return tenantService.uploadLogo(id, file);
    }

    @GetMapping("/logos/{evidenciaId}")
    public ResponseEntity<Resource> viewLogo(@PathVariable Long evidenciaId) {
        EvidenciaArquivo evidencia = tenantService.findLogo(evidenciaId);
        Resource resource = tenantService.loadLogo(evidencia);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(evidencia.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(evidencia.getNomeArquivo()).build().toString())
                .body(resource);
    }

    @RequiresPermission(resource = ResourceType.IAM_TENANT, action = Action.DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        tenantService.delete(id);
    }
}
