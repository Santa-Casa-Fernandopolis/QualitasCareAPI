package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.ProcessoReprocessamento;
import com.erp.qualitascareapi.cme.enums.ProcessoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProcessoReprocessamentoRepository extends JpaRepository<ProcessoReprocessamento, Long> {
    Page<ProcessoReprocessamento> findAllByTenantId(Long tenantId, Pageable pageable);

    long countByTenant_IdAndStatusNotIn(Long tenantId, List<ProcessoStatus> statuses);

    @Query("SELECT p.status, COUNT(p) FROM ProcessoReprocessamento p WHERE p.tenant.id = :tenantId GROUP BY p.status")
    List<Object[]> groupByStatusForTenant(@Param("tenantId") Long tenantId);
}
