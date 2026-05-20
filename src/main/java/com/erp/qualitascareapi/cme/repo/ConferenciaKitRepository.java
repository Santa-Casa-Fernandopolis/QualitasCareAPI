package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.ConferenciaKit;
import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConferenciaKitRepository extends JpaRepository<ConferenciaKit, Long> {
    List<ConferenciaKit> findByProcessoId(Long processoId);
    Optional<ConferenciaKit> findFirstByProcessoIdOrderByDataHoraConferenciaDesc(Long processoId);
    List<ConferenciaKit> findByTenantIdAndConformidade(Long tenantId, ResultadoConformidade conformidade);
    Page<ConferenciaKit> findAllByTenantId(Long tenantId, Pageable pageable);
}
