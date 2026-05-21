package com.erp.qualitascareapi.pgrss.repo;

import com.erp.qualitascareapi.pgrss.domain.TipoResiduo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoResiduoRepository extends JpaRepository<TipoResiduo, Long> {

    Page<TipoResiduo> findAllByTenant_Id(Long tenantId, Pageable pageable);

    Page<TipoResiduo> findAllByTenant_IdAndAtivo(Long tenantId, Boolean ativo, Pageable pageable);

    List<TipoResiduo> findAllByTenant_IdAndGrupoResiduo_Id(Long tenantId, Long grupoResiduoId);

    boolean existsByTenant_IdAndCodigoIgnoreCase(Long tenantId, String codigo);
}
