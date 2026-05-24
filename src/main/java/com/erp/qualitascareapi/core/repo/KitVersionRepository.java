package com.erp.qualitascareapi.core.repo;

import com.erp.qualitascareapi.core.domain.KitVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KitVersionRepository extends JpaRepository<KitVersion, Long> {
    Page<KitVersion> findAllByKit_TenantId(Long tenantId, Pageable pageable);
    Page<KitVersion> findAllByKit_IdAndKit_TenantId(Long kitId, Long tenantId, Pageable pageable);
}
