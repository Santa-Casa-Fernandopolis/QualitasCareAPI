package com.erp.qualitascareapi.ged.repo;

import com.erp.qualitascareapi.ged.domain.DocumentSignature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentSignatureRepository extends JpaRepository<DocumentSignature, Long> {
    List<DocumentSignature> findAllByDocumentVersion_IdAndTenant_IdOrderByRequestedAtAsc(Long documentVersionId, Long tenantId);
}
