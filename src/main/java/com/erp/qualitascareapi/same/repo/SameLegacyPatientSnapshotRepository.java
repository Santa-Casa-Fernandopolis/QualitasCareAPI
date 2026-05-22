package com.erp.qualitascareapi.same.repo;

import com.erp.qualitascareapi.same.domain.SameLegacyPatientSnapshot;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SameLegacyPatientSnapshotRepository extends JpaRepository<SameLegacyPatientSnapshot, Long> {

    Page<SameLegacyPatientSnapshot> findAllByTenantId(Long tenantId, Pageable pageable);

    Page<SameLegacyPatientSnapshot> findAllByTenantIdAndSourceSystem(Long tenantId, SameSourceSystem sourceSystem, Pageable pageable);

    Page<SameLegacyPatientSnapshot> findAllByTenantIdAndSourceSystemAndMedicalRecordCode(
            Long tenantId, SameSourceSystem sourceSystem, String medicalRecordCode, Pageable pageable);

    Page<SameLegacyPatientSnapshot> findAllByTenantIdAndCpf(Long tenantId, String cpf, Pageable pageable);

    Page<SameLegacyPatientSnapshot> findAllByTenantIdAndSourceSystemAndCpf(
            Long tenantId, SameSourceSystem sourceSystem, String cpf, Pageable pageable);
}
