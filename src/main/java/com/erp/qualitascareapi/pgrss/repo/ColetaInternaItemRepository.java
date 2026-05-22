package com.erp.qualitascareapi.pgrss.repo;

import com.erp.qualitascareapi.pgrss.domain.ColetaInternaItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColetaInternaItemRepository extends JpaRepository<ColetaInternaItem, Long> {

    List<ColetaInternaItem> findAllByColetaInterna_Id(Long coletaId);

    boolean existsByPesagem_Id(Long pesagemId);
}
