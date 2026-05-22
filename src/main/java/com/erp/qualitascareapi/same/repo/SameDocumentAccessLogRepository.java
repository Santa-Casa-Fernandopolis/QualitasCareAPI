package com.erp.qualitascareapi.same.repo;

import com.erp.qualitascareapi.same.domain.SameDocumentAccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SameDocumentAccessLogRepository extends JpaRepository<SameDocumentAccessLog, Long> {

    Page<SameDocumentAccessLog> findAllByTenantIdAndClinicalDocumentId(Long tenantId, Long clinicalDocumentId, Pageable pageable);

    Page<SameDocumentAccessLog> findAllByTenantIdAndPatientMasterId(Long tenantId, Long patientMasterId, Pageable pageable);
}
