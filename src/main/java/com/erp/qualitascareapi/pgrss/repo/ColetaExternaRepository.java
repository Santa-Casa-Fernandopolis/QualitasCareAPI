package com.erp.qualitascareapi.pgrss.repo;

import com.erp.qualitascareapi.pgrss.domain.ColetaExterna;
import com.erp.qualitascareapi.pgrss.enums.StatusColetaExterna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ColetaExternaRepository extends JpaRepository<ColetaExterna, Long> {

    Page<ColetaExterna> findAllByTenant_Id(Long tenantId, Pageable pageable);

    List<ColetaExterna> findAllByTenant_IdAndDataColetaBetween(Long tenantId, LocalDate inicio, LocalDate fim);

    long countByTenant_IdAndStatusNot(Long tenantId, StatusColetaExterna status);

    @Query("SELECT COUNT(c) FROM ColetaExterna c WHERE c.tenant.id = :tid AND c.status = 'REGISTRADA' AND c.numeroCertificadoDestinacao IS NULL")
    long countSemDocumento(@Param("tid") Long tid);

    @Query("""
            SELECT c FROM ColetaExterna c
            WHERE c.tenant.id = :tenantId
              AND (:empresaId IS NULL OR c.empresa.id = :empresaId)
              AND (:grupoId IS NULL OR c.grupo.id = :grupoId)
              AND (:status IS NULL OR c.status = :status)
              AND (:dataInicio IS NULL OR c.dataColeta >= :dataInicio)
              AND (:dataFim IS NULL OR c.dataColeta <= :dataFim)
            ORDER BY c.dataColeta DESC
            """)
    Page<ColetaExterna> search(
            @Param("tenantId") Long tenantId,
            @Param("empresaId") Long empresaId,
            @Param("grupoId") Long grupoId,
            @Param("status") StatusColetaExterna status,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            Pageable pageable);
}
