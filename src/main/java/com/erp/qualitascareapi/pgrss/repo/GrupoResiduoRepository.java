package com.erp.qualitascareapi.pgrss.repo;

import com.erp.qualitascareapi.pgrss.domain.GrupoResiduo;
import com.erp.qualitascareapi.pgrss.enums.GrupoResiduoCodigo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GrupoResiduoRepository extends JpaRepository<GrupoResiduo, Long> {

    Page<GrupoResiduo> findAllByTenant_Id(Long tenantId, Pageable pageable);

    List<GrupoResiduo> findAllByTenant_IdAndAtivo(Long tenantId, Boolean ativo);

    Optional<GrupoResiduo> findByTenant_IdAndCodigo(Long tenantId, GrupoResiduoCodigo codigo);

    boolean existsByTenant_IdAndCodigo(Long tenantId, GrupoResiduoCodigo codigo);
}
