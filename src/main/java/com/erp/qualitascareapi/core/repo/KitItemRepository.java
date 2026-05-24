package com.erp.qualitascareapi.core.repo;

import com.erp.qualitascareapi.core.domain.KitItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KitItemRepository extends JpaRepository<KitItem, Long> {
    Page<KitItem> findAllByVersao_Kit_TenantId(Long tenantId, Pageable pageable);
    Page<KitItem> findAllByVersao_IdAndVersao_Kit_TenantId(Long versaoId, Long tenantId, Pageable pageable);
    long countByVersao_IdAndVersao_Kit_TenantId(Long versaoId, Long tenantId);
}
