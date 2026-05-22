package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.RecebimentoMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RecebimentoMaterialRepository extends JpaRepository<RecebimentoMaterial, Long> {
    Page<RecebimentoMaterial> findAllByTenantId(Long tenantId, Pageable pageable);

    long countByTenant_IdAndDataHoraAfter(Long tenantId, LocalDateTime after);
}
