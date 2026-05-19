package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.LimpezaManual;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LimpezaManualRepository extends JpaRepository<LimpezaManual, Long> {
    List<LimpezaManual> findByProcessoId(Long processoId);
}
