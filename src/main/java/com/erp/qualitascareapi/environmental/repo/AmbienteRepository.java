package com.erp.qualitascareapi.environmental.repo;

import com.erp.qualitascareapi.environmental.domain.Ambiente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmbienteRepository extends JpaRepository<Ambiente, Long> {

    Page<Ambiente> findAllByTenantId(Long tenantId, Pageable pageable);

    Page<Ambiente> findAllByTenantIdAndAtivo(Long tenantId, boolean ativo, Pageable pageable);

    List<Ambiente> findAllByTenantIdAndAtivo(Long tenantId, boolean ativo);

    long countByTenantIdAndAtivo(Long tenantId, boolean ativo);
}
