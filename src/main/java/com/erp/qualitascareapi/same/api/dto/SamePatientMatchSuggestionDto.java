package com.erp.qualitascareapi.same.api.dto;

import com.erp.qualitascareapi.same.enums.SameConfidenceLevel;
import com.erp.qualitascareapi.same.enums.SamePatientStatus;

import java.time.LocalDate;

public record SamePatientMatchSuggestionDto(
        Long patientMasterId,
        String fullName,
        String motherName,
        LocalDate birthDate,
        String cpf,
        String cns,
        SamePatientStatus status,
        SameConfidenceLevel confidenceLevel,
        String reason
) {
}
