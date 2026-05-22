package com.erp.qualitascareapi.pgrss.repo;

import com.erp.qualitascareapi.pgrss.domain.CustoTratamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustoTratamentoRepository extends JpaRepository<CustoTratamento, Long> {

    Page<CustoTratamento> findAllByTenant_Id(Long tenantId, Pageable pageable);

    List<CustoTratamento> findAllByTenant_IdAndGrupo_IdAndAtivoTrue(Long tenantId, Long grupoId);
}
