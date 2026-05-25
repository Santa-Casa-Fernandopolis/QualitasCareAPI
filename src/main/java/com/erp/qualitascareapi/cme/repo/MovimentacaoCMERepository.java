package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.MovimentacaoCME;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoCMERepository extends JpaRepository<MovimentacaoCME, Long> {
    Page<MovimentacaoCME> findAllByTenantId(Long tenantId, Pageable pageable);
    List<MovimentacaoCME> findAllByLote_IdOrderByDataHoraAsc(Long loteId);
}
