package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.UsoSaneante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsoSaneanteRepository extends JpaRepository<UsoSaneante, Long> {
    Page<UsoSaneante> findAllByLoteSaneante_TenantId(Long tenantId, Pageable pageable);

    @Query("""
            select coalesce(sum(u.volumeUtilizadoMl), 0)
            from UsoSaneante u
            where u.loteSaneante.id = :loteId
            """)
    Double sumVolumeUtilizadoByLoteId(@Param("loteId") Long loteId);
}
