package com.erp.qualitascareapi.same.api.dto;

import com.erp.qualitascareapi.same.enums.SameConfidenceLevel;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SamePatientIdentifierRequest(
        @NotNull SameSourceSystem sourceSystem,
        @NotBlank @Size(max = 80) String medicalRecordCode,
        @Size(max = 80) String externalPatientId,
        Boolean primaryIdentifier,
        SameConfidenceLevel confidenceLevel
) {
}
