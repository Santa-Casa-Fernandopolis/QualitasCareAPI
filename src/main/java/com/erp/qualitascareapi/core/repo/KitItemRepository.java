package com.erp.qualitascareapi.core.repo;

import com.erp.qualitascareapi.core.domain.KitItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KitItemRepository extends JpaRepository<KitItem, Long> {
    Page<KitItem> findAllByVersao_Kit_TenantId(Long tenantId, Pageable pageable);
    Page<KitItem> findAllByVersao_IdAndVersao_Kit_TenantId(Long versaoId, Long tenantId, Pageable pageable);
    List<KitItem> findAllByVersao_Id(Long versaoId);
    long countByVersao_IdAndVersao_Kit_TenantId(Long versaoId, Long tenantId);
    long countByIdAndVersao_Kit_TenantId(Long id, Long tenantId);
}
