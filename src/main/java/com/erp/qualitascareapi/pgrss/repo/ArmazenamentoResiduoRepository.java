package com.erp.qualitascareapi.pgrss.repo;

import com.erp.qualitascareapi.pgrss.domain.ArmazenamentoResiduo;
import com.erp.qualitascareapi.pgrss.enums.StatusArmazenamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArmazenamentoResiduoRepository extends JpaRepository<ArmazenamentoResiduo, Long> {

    Page<ArmazenamentoResiduo> findAllByTenant_Id(Long tenantId, Pageable pageable);

    List<ArmazenamentoResiduo> findAllByTenant_IdAndStatus(Long tenantId, StatusArmazenamento status);
}
