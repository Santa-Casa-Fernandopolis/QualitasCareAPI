package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.CmeEquipamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CmeEquipamentoRepository extends JpaRepository<CmeEquipamento, Long> {
    List<CmeEquipamento> findAllByTenantIdOrderByNomeAsc(Long tenantId);
    boolean existsByTenantIdAndCodigoIgnoreCase(Long tenantId, String codigo);
}
