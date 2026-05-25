package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.KitFisicoInstrumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KitFisicoInstrumentoRepository extends JpaRepository<KitFisicoInstrumento, Long> {
    List<KitFisicoInstrumento> findAllByKitFisico_IdAndAtivoTrueOrderByInstrumentoFisico_IdentificadorUnicoAsc(Long kitFisicoId);
    Optional<KitFisicoInstrumento> findByKitFisico_IdAndInstrumentoFisico_IdAndAtivoTrue(Long kitFisicoId, Long instrumentoFisicoId);
    Optional<KitFisicoInstrumento> findByInstrumentoFisico_IdAndAtivoTrue(Long instrumentoFisicoId);
}
