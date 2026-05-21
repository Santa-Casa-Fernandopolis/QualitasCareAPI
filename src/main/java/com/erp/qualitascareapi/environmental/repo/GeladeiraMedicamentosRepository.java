package com.erp.qualitascareapi.environmental.repo;

import com.erp.qualitascareapi.environmental.domain.GeladeiraMedicamentos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeladeiraMedicamentosRepository extends JpaRepository<GeladeiraMedicamentos, Long> {
    Page<GeladeiraMedicamentos> findAllByTenantId(Long tenantId, Pageable pageable);
    Page<GeladeiraMedicamentos> findAllByTenantIdAndAtivo(Long tenantId, boolean ativo, Pageable pageable);
}
