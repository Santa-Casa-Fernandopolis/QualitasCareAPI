package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.CmeFluxoProcesso;
import com.erp.qualitascareapi.cme.enums.TipoFluxoCME;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CmeFluxoProcessoRepository extends JpaRepository<CmeFluxoProcesso, Long> {
    List<CmeFluxoProcesso> findAllByTenantIdOrderByTipoFluxoAscNumeroVersaoDesc(Long tenantId);
    Optional<CmeFluxoProcesso> findFirstByTenantIdAndTipoFluxoAndAtivoTrueOrderByNumeroVersaoDesc(Long tenantId, TipoFluxoCME tipoFluxo);
    boolean existsByTenantIdAndTipoFluxo(Long tenantId, TipoFluxoCME tipoFluxo);
}
