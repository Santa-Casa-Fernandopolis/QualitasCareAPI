package com.erp.qualitascareapi.pgrss.repo;

import com.erp.qualitascareapi.pgrss.domain.PlanoAcaoResiduo;
import com.erp.qualitascareapi.pgrss.enums.StatusPlanoAcao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PlanoAcaoResiduoRepository extends JpaRepository<PlanoAcaoResiduo, Long> {

    Page<PlanoAcaoResiduo> findAllByTenant_Id(Long tenantId, Pageable pageable);

    List<PlanoAcaoResiduo> findAllByTenant_IdAndStatusIn(Long tenantId, List<StatusPlanoAcao> statuses);

    long countByTenant_IdAndStatusIn(Long tenantId, List<StatusPlanoAcao> statuses);

    List<PlanoAcaoResiduo> findAllByTenant_IdAndDataPrazoBefore(Long tenantId, LocalDate data);
}
