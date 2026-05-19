package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.ProcessoReprocessamento;
import com.erp.qualitascareapi.cme.enums.ProcessoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessoReprocessamentoRepository extends JpaRepository<ProcessoReprocessamento, Long> {
    Page<ProcessoReprocessamento> findAllByTenantId(Long tenantId, Pageable pageable);

    long countByTenant_IdAndStatusNotIn(Long tenantId, List<ProcessoStatus> statuses);
}
