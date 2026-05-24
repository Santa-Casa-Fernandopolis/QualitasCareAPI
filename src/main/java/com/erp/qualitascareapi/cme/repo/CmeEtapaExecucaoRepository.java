package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.CmeEtapaExecucao;
import com.erp.qualitascareapi.cme.enums.CmeEtapaTipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CmeEtapaExecucaoRepository extends JpaRepository<CmeEtapaExecucao, Long> {
    List<CmeEtapaExecucao> findAllByProcessoIdOrderByEtapa_OrdemAsc(Long processoId);
    List<CmeEtapaExecucao> findAllByProcesso_Tenant_IdOrderByProcesso_DataAberturaDesc(Long tenantId);
    Optional<CmeEtapaExecucao> findFirstByProcessoIdAndEtapa_TipoEtapaOrderByEtapa_OrdemAsc(Long processoId, CmeEtapaTipo tipoEtapa);
    boolean existsByProcessoId(Long processoId);
}
