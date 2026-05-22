package com.erp.qualitascareapi.same.legacy;

import com.erp.qualitascareapi.same.enums.SameSourceSystem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LegacyPatientConnector {

    boolean supports(SameSourceSystem sourceSystem);

    List<LegacyPatientRecord> searchByCpf(Long tenantId, String cpf);

    List<LegacyPatientRecord> searchByMedicalRecordCode(Long tenantId, SameSourceSystem sourceSystem, String code);

    List<LegacyPatientRecord> searchByNameAndBirthDate(Long tenantId, String name, LocalDate birthDate);

    Optional<LegacyPatientRecord> getByExternalPatientId(Long tenantId, String id);
}
