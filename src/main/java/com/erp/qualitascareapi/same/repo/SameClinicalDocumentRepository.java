package com.erp.qualitascareapi.same.repo;

import com.erp.qualitascareapi.same.domain.SameClinicalDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SameClinicalDocumentRepository extends JpaRepository<SameClinicalDocument, Long>, JpaSpecificationExecutor<SameClinicalDocument> {

    Optional<SameClinicalDocument> findByIdAndTenantId(Long id, Long tenantId);

    Page<SameClinicalDocument> findAllByTenantIdAndPatientMasterId(Long tenantId, Long patientMasterId, Pageable pageable);
}
