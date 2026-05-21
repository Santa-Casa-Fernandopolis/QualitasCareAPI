package com.erp.qualitascareapi.environmental.repo;

import com.erp.qualitascareapi.environmental.domain.GeladeiraMedicamentos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeladeiraMedicamentosRepository extends JpaRepository<GeladeiraMedicamentos, Long> {

    Page<GeladeiraMedicamentos> findAllByTenantId(Long tenantId, Pageable pageable);

    Page<GeladeiraMedicamentos> findAllByTenantIdAndAtivo(Long tenantId, boolean ativo, Pageable pageable);

    List<GeladeiraMedicamentos> findAllByTenantIdAndAtivo(Long tenantId, boolean ativo);

    long countByTenantIdAndAtivo(Long tenantId, boolean ativo);
}
