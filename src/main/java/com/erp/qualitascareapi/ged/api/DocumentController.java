package com.erp.qualitascareapi.ged.api;

import com.erp.qualitascareapi.ged.api.dto.*;
import com.erp.qualitascareapi.ged.application.DocumentManagementService;
import com.erp.qualitascareapi.ged.enums.DocumentStatus;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/ged/documentos")
public class DocumentController {

    private final DocumentManagementService documentService;

    public DocumentController(DocumentManagementService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.DOCUMENTO, action = Action.READ)
    public Page<DocumentDto> list(@RequestParam(required = false) String search,
                                  @RequestParam(required = false) DocumentStatus status,
                                  Pageable pageable) {
        return documentService.list(search, status, pageable);
    }

    @GetMapping("/biblioteca")
    @RequiresPermission(resource = ResourceType.DOCUMENTO, action = Action.READ)
    public Page<DocumentDto> library(@RequestParam(required = false) String search, Pageable pageable) {
        return documentService.library(search, pageable);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.DOCUMENTO, action = Action.READ)
    public DocumentDto get(@PathVariable Long id) {
        return documentService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.DOCUMENTO, action = Action.CREATE)
    public DocumentDto create(@Validated @RequestBody DocumentRequest request) {
        return documentService.create(request);
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = ResourceType.DOCUMENTO, action = Action.UPDATE)
    public DocumentDto update(@PathVariable Long id, @Validated @RequestBody DocumentRequest request) {
        return documentService.update(id, request);
    }

    @PostMapping("/{documentId}/versoes")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.DOCUMENTO_VERSAO, action = Action.CREATE)
    public DocumentVersionDto createVersion(@PathVariable Long documentId,
                                            @Validated @RequestBody DocumentVersionRequest request) {
        return documentService.createVersion(documentId, request);
    }

    @GetMapping("/{documentId}/versoes")
    @RequiresPermission(resource = ResourceType.DOCUMENTO_VERSAO, action = Action.READ)
    public List<DocumentVersionDto> listVersions(@PathVariable Long documentId) {
        return documentService.listVersions(documentId);
    }

    @PostMapping("/versoes/{versionId}/arquivo-original")
    @RequiresPermission(resource = ResourceType.DOCUMENTO_VERSAO, action = Action.UPDATE)
    public DocumentVersionDto uploadOriginal(@PathVariable Long versionId,
                                             @RequestPart("file") MultipartFile file) {
        return documentService.uploadOriginal(versionId, file);
    }

    @PostMapping("/versoes/{versionId}/arquivo-publicado")
    @RequiresPermission(resource = ResourceType.DOCUMENTO_VERSAO, action = Action.UPDATE)
    public DocumentVersionDto uploadPublished(@PathVariable Long versionId,
                                              @RequestPart("file") MultipartFile file) {
        return documentService.uploadPublished(versionId, file);
    }

    @PostMapping("/versoes/{versionId}/submeter-aprovacao")
    @RequiresPermission(resource = ResourceType.DOCUMENTO_VERSAO, action = Action.APPROVE)
    public ApprovalRequestDto submitForApproval(@PathVariable Long versionId) {
        return documentService.submitForApproval(versionId);
    }

    @GetMapping("/versoes/{versionId}/aprovacao")
    @RequiresPermission(resource = ResourceType.DOCUMENTO_VERSAO, action = Action.READ)
    public ApprovalRequestDto getApproval(@PathVariable Long versionId) {
        return documentService.getApproval(versionId);
    }

    @PostMapping("/aprovacoes/etapas/{stepId}/decidir")
    @RequiresPermission(resource = ResourceType.DOCUMENTO_VERSAO, action = Action.APPROVE)
    public ApprovalRequestDto decide(@PathVariable Long stepId,
                                     @Validated @RequestBody ApprovalDecisionRequest request) {
        return documentService.decide(stepId, request);
    }

    @GetMapping("/aprovacoes/{approvalRequestId}/historico")
    @RequiresPermission(resource = ResourceType.DOCUMENTO_VERSAO, action = Action.READ)
    public List<ApprovalDecisionDto> approvalHistory(@PathVariable Long approvalRequestId) {
        return documentService.approvalHistory(approvalRequestId);
    }

    @PostMapping("/versoes/{versionId}/publicar")
    @RequiresPermission(resource = ResourceType.DOCUMENTO_VERSAO, action = Action.APPROVE)
    public DocumentVersionDto publish(@PathVariable Long versionId,
                                      @RequestBody PublishDocumentRequest request) {
        return documentService.publish(versionId, request);
    }

    @PostMapping("/versoes/{versionId}/assinaturas")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.DOCUMENTO_ACK, action = Action.CREATE)
    public DocumentSignatureDto requestSignature(@PathVariable Long versionId,
                                                 @Validated @RequestBody DocumentSignatureRequest request) {
        return documentService.requestSignature(versionId, request);
    }

    @GetMapping("/versoes/{versionId}/assinaturas")
    @RequiresPermission(resource = ResourceType.DOCUMENTO_ACK, action = Action.READ)
    public List<DocumentSignatureDto> listSignatures(@PathVariable Long versionId) {
        return documentService.listSignatures(versionId);
    }

    @PostMapping("/assinaturas/{signatureId}/assinar")
    @RequiresPermission(resource = ResourceType.DOCUMENTO_ACK, action = Action.CREATE)
    public DocumentSignatureDto sign(@PathVariable Long signatureId,
                                     @RequestParam(required = false) String comment) {
        return documentService.sign(signatureId, comment);
    }
}
