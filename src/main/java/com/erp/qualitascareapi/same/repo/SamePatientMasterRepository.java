package com.erp.qualitascareapi.same.repo;

import com.erp.qualitascareapi.same.domain.SamePatientMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface SamePatientMasterRepository extends JpaRepository<SamePatientMaster, Long> {

    Optional<SamePatientMaster> findByIdAndTenantId(Long id, Long tenantId);

    Page<SamePatientMaster> findAllByTenantId(Long tenantId, Pageable pageable);

    @Query("""
            SELECT DISTINCT p FROM SamePatientMaster p
            LEFT JOIN SamePatientIdentifier i ON i.patientMaster = p
            WHERE p.tenant.id = :tenantId
              AND (
                :query IS NULL
                OR :query = ''
                OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(COALESCE(p.motherName, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                OR p.cpf = :query
                OR p.cns = :query
                OR i.medicalRecordCode = :query
                OR i.externalPatientId = :query
              )
            """)
    Page<SamePatientMaster> search(@Param("tenantId") Long tenantId, @Param("query") String query, Pageable pageable);

    Page<SamePatientMaster> findAllByTenantIdAndCpf(Long tenantId, String cpf, Pageable pageable);

    Page<SamePatientMaster> findAllByTenantIdAndCns(Long tenantId, String cns, Pageable pageable);

    Page<SamePatientMaster> findAllByTenantIdAndBirthDate(Long tenantId, LocalDate birthDate, Pageable pageable);
}
