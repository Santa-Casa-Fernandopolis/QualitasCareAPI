package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.ManutencaoAutoclave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManutencaoAutoclaveRepository extends JpaRepository<ManutencaoAutoclave, Long> {
    Page<ManutencaoAutoclave> findAllByAutoclave_TenantId(Long tenantId, Pageable pageable);
}
