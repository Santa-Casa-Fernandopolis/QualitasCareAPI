package com.erp.qualitascareapi.pgrss.repo;

import com.erp.qualitascareapi.pgrss.domain.DocumentoResiduo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoResiduoRepository extends JpaRepository<DocumentoResiduo, Long> {

    List<DocumentoResiduo> findAllByColetaExterna_IdAndAtivoTrue(Long coletaExternaId);
}
