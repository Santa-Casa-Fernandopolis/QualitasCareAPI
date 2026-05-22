package com.erp.qualitascareapi.same.api;

import com.erp.qualitascareapi.same.api.dto.SameClinicalDocumentDto;
import com.erp.qualitascareapi.same.api.dto.SameClinicalDocumentMetadataRequest;
import com.erp.qualitascareapi.same.application.SameClinicalDocumentService;
import com.erp.qualitascareapi.same.enums.SameDocumentStatus;
import com.erp.qualitascareapi.same.enums.SameDocumentType;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/same/documents")
public class SameClinicalDocumentController {

    private final SameClinicalDocumentService documentService;

    public SameClinicalDocumentController(SameClinicalDocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.SAME_DOCUMENT, action = Action.CREATE)
    public SameClinicalDocumentDto upload(@RequestParam Long patientMasterId,
                                          @RequestParam(required = false) Long patientIdentifierId,
                                          @RequestParam SameDocumentType documentType,
                                          @RequestParam SameSourceSystem sourceSystem,
                                          @RequestParam(required = false) String originalMedicalRecordCode,
                                          @RequestParam(required = false) String attendanceCode,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate attendanceDate,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate documentPeriodStart,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate documentPeriodEnd,
                                          @RequestParam(required = false) String description,
                                          @RequestParam MultipartFile file,
                                          HttpServletRequest request) {
        return documentService.upload(patientMasterId, patientIdentifierId, documentType, sourceSystem,
                originalMedicalRecordCode, attendanceCode, attendanceDate, documentPeriodStart, documentPeriodEnd,
                description, file, request);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.SAME_DOCUMENT, action = Action.READ)
    public SameClinicalDocumentDto findById(@PathVariable Long id, HttpServletRequest request) {
        return documentService.findById(id, request);
    }

    @GetMapping("/search")
    @RequiresPermission(resource = ResourceType.SAME_DOCUMENT, action = Action.READ)
    public Page<SameClinicalDocumentDto> search(@RequestParam(required = false) Long patientMasterId,
                                                @RequestParam(required = false) String patientName,
                                                @RequestParam(required = false) String cpf,
                                                @RequestParam(required = false) String medicalRecordCode,
                                                @RequestParam(required = false) SameSourceSystem sourceSystem,
                                                @RequestParam(required = false) SameDocumentType documentType,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd,
                                                @RequestParam(required = false) String attendanceCode,
                                                @RequestParam(required = false) SameDocumentStatus status,
                                                Pageable pageable) {
        return documentService.search(patientMasterId, patientName, cpf, medicalRecordCode, sourceSystem,
                documentType, periodStart, periodEnd, attendanceCode, status, pageable);
    }

    @PutMapping("/{id}/metadata")
    @RequiresPermission(resource = ResourceType.SAME_DOCUMENT, action = Action.UPDATE)
    public SameClinicalDocumentDto updateMetadata(@PathVariable Long id,
                                                  @Valid @RequestBody SameClinicalDocumentMetadataRequest metadata,
                                                  HttpServletRequest request) {
        return documentService.updateMetadata(id, metadata, request);
    }

    @PatchMapping("/{id}/archive")
    @RequiresPermission(resource = ResourceType.SAME_DOCUMENT, action = Action.ARCHIVE)
    public SameClinicalDocumentDto archive(@PathVariable Long id, HttpServletRequest request) {
        return documentService.archive(id, request);
    }

    @PatchMapping("/{id}/block")
    @RequiresPermission(resource = ResourceType.SAME_DOCUMENT, action = Action.BLOCK)
    public SameClinicalDocumentDto block(@PathVariable Long id, HttpServletRequest request) {
        return documentService.block(id, request);
    }

    @GetMapping("/{id}/download")
    @RequiresPermission(resource = ResourceType.SAME_DOCUMENT, action = Action.DOWNLOAD)
    public ResponseEntity<Resource> download(@PathVariable Long id, HttpServletRequest request) {
        Resource resource = documentService.download(id, request);
        String filename = resource.getFilename() != null ? resource.getFilename() : "documento-same.pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(resource);
    }
}
