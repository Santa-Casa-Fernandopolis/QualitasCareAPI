package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.AprovacaoLoteInalatorio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AprovacaoLoteInalatorioRepository extends JpaRepository<AprovacaoLoteInalatorio, Long> {
    Page<AprovacaoLoteInalatorio> findAllByTenantId(Long tenantId, Pageable pageable);
}
