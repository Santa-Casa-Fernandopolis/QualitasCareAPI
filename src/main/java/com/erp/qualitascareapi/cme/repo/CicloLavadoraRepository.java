package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.CicloLavadora;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CicloLavadoraRepository extends JpaRepository<CicloLavadora, Long> {
    Page<CicloLavadora> findAllByTenantId(Long tenantId, Pageable pageable);
}
