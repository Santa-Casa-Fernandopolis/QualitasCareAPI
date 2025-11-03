package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
}
