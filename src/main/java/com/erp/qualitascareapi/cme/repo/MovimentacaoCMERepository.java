package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.MovimentacaoCME;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoCMERepository extends JpaRepository<MovimentacaoCME, Long> {
    Page<MovimentacaoCME> findAllByTenantId(Long tenantId, Pageable pageable);
}
