package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.PlanoPreventivoAutoclave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanoPreventivoAutoclaveRepository extends JpaRepository<PlanoPreventivoAutoclave, Long> {
    Page<PlanoPreventivoAutoclave> findAllByAutoclave_TenantId(Long tenantId, Pageable pageable);
}
