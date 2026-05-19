package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.IndicadorBiologico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndicadorBiologicoRepository extends JpaRepository<IndicadorBiologico, Long> {
    Page<IndicadorBiologico> findAllByCiclo_TenantId(Long tenantId, Pageable pageable);
}
