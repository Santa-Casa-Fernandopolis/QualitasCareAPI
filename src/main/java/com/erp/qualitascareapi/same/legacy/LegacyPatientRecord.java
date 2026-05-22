package com.erp.qualitascareapi.same.legacy;

import com.erp.qualitascareapi.same.enums.SameSex;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;

import java.time.LocalDate;

public record LegacyPatientRecord(
        SameSourceSystem sourceSystem,
        String externalPatientId,
        String medicalRecordCode,
        String fullName,
        String motherName,
        LocalDate birthDate,
        String cpf,
        String cns,
        SameSex sex,
        String rawPayloadJson
) {
}
