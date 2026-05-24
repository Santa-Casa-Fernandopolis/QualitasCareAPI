package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.CmeEtapaProcesso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CmeEtapaProcessoRepository extends JpaRepository<CmeEtapaProcesso, Long> {
    List<CmeEtapaProcesso> findAllByFluxoProcessoIdOrderByOrdemAsc(Long fluxoProcessoId);
}
