package com.erp.qualitascareapi.core.repo;

import com.erp.qualitascareapi.core.domain.Instrumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentoRepository extends JpaRepository<Instrumento, Long> {
    Page<Instrumento> findAllByTenantId(Long tenantId, Pageable pageable);
}
