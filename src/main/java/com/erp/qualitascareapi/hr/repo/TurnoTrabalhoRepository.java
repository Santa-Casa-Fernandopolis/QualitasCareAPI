package com.erp.qualitascareapi.hr.repo;

import com.erp.qualitascareapi.hr.domain.TurnoTrabalho;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TurnoTrabalhoRepository extends JpaRepository<TurnoTrabalho, Long> {
    Page<TurnoTrabalho> findAllByTenant_Id(Long tenantId, Pageable pageable);
    Optional<TurnoTrabalho> findByTenant_IdAndCodigoIgnoreCase(Long tenantId, String codigo);
}
