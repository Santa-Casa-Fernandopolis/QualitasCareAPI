package com.erp.qualitascareapi.core.repo;

import com.erp.qualitascareapi.core.domain.KitVersion;
import com.erp.qualitascareapi.cme.enums.StatusAprovacaoCme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KitVersionRepository extends JpaRepository<KitVersion, Long> {
    Page<KitVersion> findAllByKit_TenantId(Long tenantId, Pageable pageable);
    Page<KitVersion> findAllByKit_IdAndKit_TenantId(Long kitId, Long tenantId, Pageable pageable);
    List<KitVersion> findAllByKit_IdAndAtivoTrueAndStatusAprovacao(Long kitId, StatusAprovacaoCme statusAprovacao);
}
