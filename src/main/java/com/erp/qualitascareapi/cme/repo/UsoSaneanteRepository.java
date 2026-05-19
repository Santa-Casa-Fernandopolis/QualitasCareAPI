package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.UsoSaneante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsoSaneanteRepository extends JpaRepository<UsoSaneante, Long> {
    Page<UsoSaneante> findAllByLoteSaneante_TenantId(Long tenantId, Pageable pageable);
}
