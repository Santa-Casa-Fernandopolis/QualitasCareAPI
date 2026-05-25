package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.CmeEtapaCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CmeEtapaCatalogoRepository extends JpaRepository<CmeEtapaCatalogo, Long> {
    List<CmeEtapaCatalogo> findAllByTenantIdOrderByNomeAsc(Long tenantId);
    boolean existsByTenantIdAndCodigoIgnoreCase(Long tenantId, String codigo);
}
