package com.erp.qualitascareapi.same.repo;

import com.erp.qualitascareapi.same.domain.SameLegacyPatientSnapshot;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SameLegacyPatientSnapshotRepository extends JpaRepository<SameLegacyPatientSnapshot, Long> {

    Page<SameLegacyPatientSnapshot> findAllByTenantIdAndSourceSystemAndMedicalRecordCode(
            Long tenantId, SameSourceSystem sourceSystem, String medicalRecordCode, Pageable pageable);

    Page<SameLegacyPatientSnapshot> findAllByTenantIdAndCpf(Long tenantId, String cpf, Pageable pageable);
}
