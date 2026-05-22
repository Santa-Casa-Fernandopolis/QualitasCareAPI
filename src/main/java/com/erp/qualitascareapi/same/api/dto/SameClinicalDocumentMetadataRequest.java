package com.erp.qualitascareapi.same.api.dto;

import com.erp.qualitascareapi.same.enums.SameDocumentType;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SameClinicalDocumentMetadataRequest(
        Long patientIdentifierId,
        @NotNull SameDocumentType documentType,
        @NotNull SameSourceSystem sourceSystem,
        @Size(max = 80) String originalMedicalRecordCode,
        @Size(max = 80) String attendanceCode,
        LocalDate attendanceDate,
        LocalDate documentPeriodStart,
        LocalDate documentPeriodEnd,
        @Size(max = 1000) String description
) {
}
