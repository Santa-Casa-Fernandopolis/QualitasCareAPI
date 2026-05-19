package com.erp.qualitascareapi.core.repo;

import com.erp.qualitascareapi.core.domain.KitProcedimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KitProcedimentoRepository extends JpaRepository<KitProcedimento, Long> {
    Page<KitProcedimento> findAllByTenantId(Long tenantId, Pageable pageable);
}
