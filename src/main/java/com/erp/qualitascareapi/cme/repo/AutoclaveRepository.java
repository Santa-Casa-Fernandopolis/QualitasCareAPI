package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.Autoclave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoclaveRepository extends JpaRepository<Autoclave, Long> {
    Page<Autoclave> findAllByTenantId(Long tenantId, Pageable pageable);
}
