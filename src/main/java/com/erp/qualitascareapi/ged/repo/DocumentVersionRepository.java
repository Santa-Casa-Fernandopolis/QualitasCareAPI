package com.erp.qualitascareapi.ged.repo;

import com.erp.qualitascareapi.ged.domain.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long>, JpaSpecificationExecutor<DocumentVersion> {
    Optional<DocumentVersion> findByIdAndTenant_Id(Long id, Long tenantId);
    List<DocumentVersion> findAllByDocumento_IdAndTenant_IdOrderByVersaoMajorDescVersaoMinorDesc(Long documentoId, Long tenantId);
}
