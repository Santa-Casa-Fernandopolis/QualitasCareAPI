package com.erp.qualitascareapi.same.api.dto;

import com.erp.qualitascareapi.same.enums.SameConfidenceLevel;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;

import java.time.LocalDateTime;

public record SamePatientIdentifierDto(
        Long id,
        Long patientMasterId,
        SameSourceSystem sourceSystem,
        String medicalRecordCode,
        String externalPatientId,
        boolean primaryIdentifier,
        SameConfidenceLevel confidenceLevel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
