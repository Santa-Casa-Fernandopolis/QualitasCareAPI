package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.MonitoramentoAmbiental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoramentoAmbientalRepository extends JpaRepository<MonitoramentoAmbiental, Long> {
    Page<MonitoramentoAmbiental> findAllByTenantId(Long tenantId, Pageable pageable);
}
