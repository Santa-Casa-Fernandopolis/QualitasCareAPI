package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.HigienizacaoUltrassonica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HigienizacaoUltrassonicaRepository extends JpaRepository<HigienizacaoUltrassonica, Long> {
    List<HigienizacaoUltrassonica> findByProcessoId(Long processoId);
    Page<HigienizacaoUltrassonica> findAllByTenantId(Long tenantId, Pageable pageable);
}
