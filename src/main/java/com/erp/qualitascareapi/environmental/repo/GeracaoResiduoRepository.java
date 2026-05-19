package com.erp.qualitascareapi.environmental.repo;

import com.erp.qualitascareapi.environmental.domain.GeracaoResiduo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeracaoResiduoRepository extends JpaRepository<GeracaoResiduo, Long> {
    Page<GeracaoResiduo> findAllByTenantId(Long tenantId, Pageable pageable);
}
