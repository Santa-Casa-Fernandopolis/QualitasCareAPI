package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.RecebimentoMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecebimentoMaterialRepository extends JpaRepository<RecebimentoMaterial, Long> {
    Page<RecebimentoMaterial> findAllByTenantId(Long tenantId, Pageable pageable);
}
