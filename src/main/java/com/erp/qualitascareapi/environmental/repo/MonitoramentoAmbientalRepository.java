package com.erp.qualitascareapi.environmental.repo;

import com.erp.qualitascareapi.environmental.domain.MonitoramentoAmbiental;
import com.erp.qualitascareapi.environmental.enums.TipoAmbiente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoramentoAmbientalRepository extends JpaRepository<MonitoramentoAmbiental, Long> {
    Page<MonitoramentoAmbiental> findAllByTenantId(Long tenantId, Pageable pageable);
    Page<MonitoramentoAmbiental> findAllByTenantIdAndTipoAmbiente(Long tenantId, TipoAmbiente tipoAmbiente, Pageable pageable);
}
