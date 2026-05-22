package com.erp.qualitascareapi.same.repo;

import com.erp.qualitascareapi.same.domain.SamePatientIdentifier;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SamePatientIdentifierRepository extends JpaRepository<SamePatientIdentifier, Long> {

    List<SamePatientIdentifier> findAllByTenantIdAndPatientMasterId(Long tenantId, Long patientMasterId);

    Optional<SamePatientIdentifier> findByIdAndTenantIdAndPatientMasterId(Long id, Long tenantId, Long patientMasterId);

    boolean existsByTenantIdAndSourceSystemAndMedicalRecordCode(Long tenantId, SameSourceSystem sourceSystem, String medicalRecordCode);

    Optional<SamePatientIdentifier> findByTenantIdAndSourceSystemAndMedicalRecordCode(
            Long tenantId, SameSourceSystem sourceSystem, String medicalRecordCode);
}
