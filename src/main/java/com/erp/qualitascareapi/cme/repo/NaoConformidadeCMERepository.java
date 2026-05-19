package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.quality.domain.NaoConformidadeCME;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NaoConformidadeCMERepository extends JpaRepository<NaoConformidadeCME, Long> {
    Page<NaoConformidadeCME> findAllByTenantId(Long tenantId, Pageable pageable);
}
