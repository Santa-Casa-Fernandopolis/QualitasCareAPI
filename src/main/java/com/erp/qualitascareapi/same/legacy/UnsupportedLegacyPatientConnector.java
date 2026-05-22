package com.erp.qualitascareapi.same.legacy;

import com.erp.qualitascareapi.same.enums.SameSourceSystem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

abstract class UnsupportedLegacyPatientConnector implements LegacyPatientConnector {

    protected abstract SameSourceSystem sourceSystem();

    @Override
    public boolean supports(SameSourceSystem sourceSystem) {
        return sourceSystem() == sourceSystem;
    }

    @Override
    public List<LegacyPatientRecord> searchByCpf(String cpf) {
        throw unsupported();
    }

    @Override
    public List<LegacyPatientRecord> searchByMedicalRecordCode(SameSourceSystem sourceSystem, String code) {
        throw unsupported();
    }

    @Override
    public List<LegacyPatientRecord> searchByNameAndBirthDate(String name, LocalDate birthDate) {
        throw unsupported();
    }

    @Override
    public Optional<LegacyPatientRecord> getByExternalPatientId(String id) {
        throw unsupported();
    }

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("Conector legado " + sourceSystem().name() + " ainda não configurado.");
    }
}
