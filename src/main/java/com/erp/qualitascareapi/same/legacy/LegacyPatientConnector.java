package com.erp.qualitascareapi.same.legacy;

import com.erp.qualitascareapi.same.enums.SameSourceSystem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LegacyPatientConnector {

    boolean supports(SameSourceSystem sourceSystem);

    List<LegacyPatientRecord> searchByCpf(String cpf);

    List<LegacyPatientRecord> searchByMedicalRecordCode(SameSourceSystem sourceSystem, String code);

    List<LegacyPatientRecord> searchByNameAndBirthDate(String name, LocalDate birthDate);

    Optional<LegacyPatientRecord> getByExternalPatientId(String id);
}
