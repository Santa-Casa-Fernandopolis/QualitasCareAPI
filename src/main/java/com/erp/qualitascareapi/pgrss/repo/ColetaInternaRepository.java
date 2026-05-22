package com.erp.qualitascareapi.pgrss.repo;

import com.erp.qualitascareapi.pgrss.domain.ColetaInterna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColetaInternaRepository extends JpaRepository<ColetaInterna, Long> {

    Page<ColetaInterna> findAllByTenant_Id(Long tenantId, Pageable pageable);
}
