package com.erp.qualitascareapi.ged.repo;

import com.erp.qualitascareapi.ged.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
    Optional<Document> findByIdAndTenant_Id(Long id, Long tenantId);
    Optional<Document> findByTenant_IdAndCodigoIgnoreCase(Long tenantId, String codigo);
}
