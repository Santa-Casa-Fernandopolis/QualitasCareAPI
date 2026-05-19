package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.CicloEsterilizacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CicloEsterilizacaoRepository extends JpaRepository<CicloEsterilizacao, Long> {
    List<CicloEsterilizacao> findByProcessoId(Long processoId);
}
