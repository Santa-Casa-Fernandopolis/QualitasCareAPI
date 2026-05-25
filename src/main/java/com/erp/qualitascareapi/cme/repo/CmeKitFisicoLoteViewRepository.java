package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.CmeKitFisicoLoteView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CmeKitFisicoLoteViewRepository extends JpaRepository<CmeKitFisicoLoteView, Long> {
    Page<CmeKitFisicoLoteView> findAllByTenantIdAndKitFisicoId(Long tenantId, Long kitFisicoId, Pageable pageable);
    Page<CmeKitFisicoLoteView> findAllByTenantIdAndKitFisicoIdentificadorIgnoreCase(Long tenantId, String identificador, Pageable pageable);
}
