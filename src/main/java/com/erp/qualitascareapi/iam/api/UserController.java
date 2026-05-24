package com.erp.qualitascareapi.iam.api;

import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.iam.api.dto.UserCreateRequest;
import com.erp.qualitascareapi.iam.api.dto.UserDto;
import com.erp.qualitascareapi.iam.api.dto.UserUpdateRequest;
import com.erp.qualitascareapi.iam.application.UserService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.ResourceType;
import com.erp.qualitascareapi.security.enums.UserStatus;
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
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequiresPermission(resource = ResourceType.IAM_USER, action = Action.READ)
    @GetMapping
    public Page<UserDto> list(@RequestParam(required = false) String username,
                              @RequestParam(required = false) String fullName,
                              @RequestParam(required = false) UserStatus status,
                              @RequestParam(required = false) IdentityOrigin origin,
                              @RequestParam(required = false) Long tenantId,
                              Pageable pageable) {
        return userService.list(username, fullName, status, origin, tenantId, pageable);
    }

    @RequiresPermission(resource = ResourceType.IAM_USER, action = Action.READ)
    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        return userService.get(id);
    }

    @RequiresPermission(resource = ResourceType.IAM_USER, action = Action.CREATE)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Validated @RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @RequiresPermission(resource = ResourceType.IAM_USER, action = Action.UPDATE)
    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @Validated @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }

    @RequiresPermission(resource = ResourceType.IAM_USER, action = Action.UPDATE)
    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDto uploadPhoto(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        return userService.uploadPhoto(id, file);
    }

    @GetMapping("/photos/{evidenciaId}")
    public ResponseEntity<Resource> loadPhoto(@PathVariable Long evidenciaId) {
        EvidenciaArquivo evidencia = userService.findPhoto(evidenciaId);
        Resource resource = userService.loadPhoto(evidencia);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(evidencia.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(evidencia.getNomeArquivo())
                        .build()
                        .toString())
                .body(resource);
    }

    @RequiresPermission(resource = ResourceType.IAM_USER, action = Action.DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
