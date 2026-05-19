package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.SaneantePeraceticoLote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaneantePeraceticoLoteRepository extends JpaRepository<SaneantePeraceticoLote, Long> {
    Page<SaneantePeraceticoLote> findAllByTenantId(Long tenantId, Pageable pageable);
}
