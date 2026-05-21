package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.LoteEtiqueta;
import com.erp.qualitascareapi.cme.enums.LoteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoteEtiquetaRepository extends JpaRepository<LoteEtiqueta, Long> {
    List<LoteEtiqueta> findByProcessoId(Long processoId);
    Optional<LoteEtiqueta> findFirstByProcessoId(Long processoId);
    Page<LoteEtiqueta> findAllByTenantId(Long tenantId, Pageable pageable);

    long countByTenant_IdAndStatusIn(Long tenantId, List<LoteStatus> statuses);

    long countByTenant_IdAndValidadeBetweenAndStatusIn(Long tenantId, LocalDate from, LocalDate to, List<LoteStatus> statuses);

    @Query("SELECT l.status, COUNT(l) FROM LoteEtiqueta l WHERE l.tenant.id = :tenantId GROUP BY l.status")
    List<Object[]> groupByStatusForTenant(@Param("tenantId") Long tenantId);
}
