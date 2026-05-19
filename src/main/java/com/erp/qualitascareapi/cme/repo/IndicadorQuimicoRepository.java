package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.IndicadorQuimico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndicadorQuimicoRepository extends JpaRepository<IndicadorQuimico, Long> {
    Page<IndicadorQuimico> findAllByCiclo_TenantId(Long tenantId, Pageable pageable);
}
