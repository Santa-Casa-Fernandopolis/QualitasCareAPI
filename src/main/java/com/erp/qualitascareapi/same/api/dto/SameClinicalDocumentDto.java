package com.erp.qualitascareapi.same.api.dto;

import com.erp.qualitascareapi.same.enums.SameDocumentStatus;
import com.erp.qualitascareapi.same.enums.SameDocumentType;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SameClinicalDocumentDto(
        Long id,
        Long tenantId,
        Long patientMasterId,
        String patientFullName,
        String patientCpf,
        Long patientIdentifierId,
        SameDocumentType documentType,
        SameSourceSystem sourceSystem,
        String originalMedicalRecordCode,
        String attendanceCode,
        LocalDate attendanceDate,
        LocalDate documentPeriodStart,
        LocalDate documentPeriodEnd,
        String fileName,
        String fileHashSha256,
        String mimeType,
        Long fileSize,
        boolean legalValue,
        String legalValueNote,
        String description,
        SameDocumentStatus status,
        Long createdById,
        String createdByName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
