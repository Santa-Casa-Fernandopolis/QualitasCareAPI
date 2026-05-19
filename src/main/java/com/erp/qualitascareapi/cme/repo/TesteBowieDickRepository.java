package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.TesteBowieDick;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TesteBowieDickRepository extends JpaRepository<TesteBowieDick, Long> {
    Page<TesteBowieDick> findAllByAutoclave_TenantId(Long tenantId, Pageable pageable);
}
