package com.erp.qualitascareapi.same.api.dto;

import com.erp.qualitascareapi.same.enums.SameSex;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SameLegacyPatientSnapshotDto(
        Long id,
        Long tenantId,
        SameSourceSystem sourceSystem,
        String externalPatientId,
        String medicalRecordCode,
        String fullName,
        String motherName,
        LocalDate birthDate,
        String cpf,
        String cns,
        SameSex sex,
        String rawPayloadJson,
        LocalDateTime importedAt
) {
}
