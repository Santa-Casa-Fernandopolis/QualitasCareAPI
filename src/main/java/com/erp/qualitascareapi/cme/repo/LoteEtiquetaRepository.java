package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.LoteEtiqueta;
import com.erp.qualitascareapi.cme.enums.LoteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoteEtiquetaRepository extends JpaRepository<LoteEtiqueta, Long> {
    List<LoteEtiqueta> findByProcessoId(Long processoId);
    Optional<LoteEtiqueta> findFirstByProcessoId(Long processoId);
    Page<LoteEtiqueta> findAllByTenantId(Long tenantId, Pageable pageable);

    long countByTenant_IdAndStatusIn(Long tenantId, List<LoteStatus> statuses);
}
