package com.erp.qualitascareapi.same.api.dto;

import com.erp.qualitascareapi.same.enums.SamePatientStatus;
import com.erp.qualitascareapi.same.enums.SameSex;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SamePatientDto(
        Long id,
        Long tenantId,
        String fullName,
        String motherName,
        LocalDate birthDate,
        String cpf,
        String cns,
        SameSex sex,
        SamePatientStatus status,
        List<SamePatientIdentifierDto> identifiers,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
