package com.erp.qualitascareapi.same.repo;

import com.erp.qualitascareapi.same.domain.SameLegacyIntegrationSource;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SameLegacyIntegrationSourceRepository extends JpaRepository<SameLegacyIntegrationSource, Long> {

    Optional<SameLegacyIntegrationSource> findByIdAndTenantId(Long id, Long tenantId);

    Page<SameLegacyIntegrationSource> findAllByTenantId(Long tenantId, Pageable pageable);

    Page<SameLegacyIntegrationSource> findAllByTenantIdAndSourceSystem(Long tenantId, SameSourceSystem sourceSystem, Pageable pageable);

    Page<SameLegacyIntegrationSource> findAllByTenantIdAndActive(Long tenantId, boolean active, Pageable pageable);

    Page<SameLegacyIntegrationSource> findAllByTenantIdAndSourceSystemAndActive(
            Long tenantId, SameSourceSystem sourceSystem, boolean active, Pageable pageable);

    List<SameLegacyIntegrationSource> findAllByTenantIdAndSourceSystemAndActiveTrue(Long tenantId, SameSourceSystem sourceSystem);
}
