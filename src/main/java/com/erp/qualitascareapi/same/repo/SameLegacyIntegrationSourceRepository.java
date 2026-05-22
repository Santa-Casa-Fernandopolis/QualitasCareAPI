package com.erp.qualitascareapi.same.repo;

import com.erp.qualitascareapi.same.domain.SameLegacyIntegrationSource;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SameLegacyIntegrationSourceRepository extends JpaRepository<SameLegacyIntegrationSource, Long> {

    Optional<SameLegacyIntegrationSource> findByIdAndTenantId(Long id, Long tenantId);

    Page<SameLegacyIntegrationSource> findAllByTenantId(Long tenantId, Pageable pageable);

    Optional<SameLegacyIntegrationSource> findByTenantIdAndSourceSystemAndActiveTrue(Long tenantId, SameSourceSystem sourceSystem);
}
