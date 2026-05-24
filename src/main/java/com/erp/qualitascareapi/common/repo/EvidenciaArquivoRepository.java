package com.erp.qualitascareapi.common.repo;

import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EvidenciaArquivoRepository extends JpaRepository<EvidenciaArquivo, Long> {
    List<EvidenciaArquivo> findAllByTenant_IdAndIdIn(Long tenantId, Collection<Long> ids);
    Optional<EvidenciaArquivo> findByTenant_IdAndId(Long tenantId, Long id);
}
