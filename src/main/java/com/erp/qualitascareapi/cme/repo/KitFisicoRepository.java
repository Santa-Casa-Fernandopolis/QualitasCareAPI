package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.KitFisico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KitFisicoRepository extends JpaRepository<KitFisico, Long> {
    Page<KitFisico> findAllByTenant_Id(Long tenantId, Pageable pageable);
    Optional<KitFisico> findByTenant_IdAndIdentificadorUnicoIgnoreCase(Long tenantId, String identificadorUnico);
    boolean existsByTenant_IdAndIdentificadorUnicoIgnoreCase(Long tenantId, String identificadorUnico);
}
