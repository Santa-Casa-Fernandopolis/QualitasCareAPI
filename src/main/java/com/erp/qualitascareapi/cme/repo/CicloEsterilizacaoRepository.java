package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.CicloEsterilizacao;
import com.erp.qualitascareapi.cme.enums.CicloStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CicloEsterilizacaoRepository extends JpaRepository<CicloEsterilizacao, Long> {
    List<CicloEsterilizacao> findByProcessoId(Long processoId);
    Page<CicloEsterilizacao> findAllByTenantId(Long tenantId, Pageable pageable);

    long countByTenant_IdAndInicioAfter(Long tenantId, LocalDateTime after);

    long countByTenant_IdAndStatusInAndInicioAfter(Long tenantId, List<CicloStatus> statuses, LocalDateTime after);

    @Query("SELECT AVG(c.duracaoMinutos) FROM CicloEsterilizacao c WHERE c.tenant.id = :tenantId AND c.inicio > :after AND c.duracaoMinutos IS NOT NULL")
    Double avgDuracaoMinutosByTenantAndInicioAfter(@Param("tenantId") Long tenantId, @Param("after") LocalDateTime after);

    @Query("SELECT c.status, COUNT(c) FROM CicloEsterilizacao c WHERE c.tenant.id = :tenantId GROUP BY c.status")
    List<Object[]> groupByStatusForTenant(@Param("tenantId") Long tenantId);
}
