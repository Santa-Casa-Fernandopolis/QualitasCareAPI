package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.InstrumentoFisico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstrumentoFisicoRepository extends JpaRepository<InstrumentoFisico, Long> {
    Page<InstrumentoFisico> findAllByTenant_Id(Long tenantId, Pageable pageable);
    Optional<InstrumentoFisico> findByTenant_IdAndIdentificadorUnicoIgnoreCase(Long tenantId, String identificadorUnico);
    boolean existsByTenant_IdAndIdentificadorUnicoIgnoreCase(Long tenantId, String identificadorUnico);
}
