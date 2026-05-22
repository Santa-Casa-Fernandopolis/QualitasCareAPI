package com.erp.qualitascareapi.pgrss.repo;

import com.erp.qualitascareapi.pgrss.domain.NaoConformidadeResiduo;
import com.erp.qualitascareapi.pgrss.enums.SeveridadeNaoConformidade;
import com.erp.qualitascareapi.pgrss.enums.StatusNaoConformidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NaoConformidadeResiduoRepository extends JpaRepository<NaoConformidadeResiduo, Long> {

    Page<NaoConformidadeResiduo> findAllByTenant_Id(Long tenantId, Pageable pageable);

    @Query("""
            SELECT n FROM NaoConformidadeResiduo n
            WHERE n.tenant.id = :tenantId
              AND (:setorId IS NULL OR n.setor.id = :setorId)
              AND (:severidade IS NULL OR n.severidade = :severidade)
              AND (:status IS NULL OR n.status = :status)
            ORDER BY n.dataHoraOcorrencia DESC
            """)
    Page<NaoConformidadeResiduo> search(
            @Param("tenantId") Long tenantId,
            @Param("setorId") Long setorId,
            @Param("severidade") SeveridadeNaoConformidade severidade,
            @Param("status") StatusNaoConformidade status,
            Pageable pageable);

    @Query("SELECT n.setor.nome, COUNT(n) FROM NaoConformidadeResiduo n WHERE n.tenant.id = :tid AND n.dataHoraOcorrencia BETWEEN :ini AND :fim GROUP BY n.setor.nome")
    List<Object[]> countBySetor(@Param("tid") Long tid, @Param("ini") LocalDateTime ini, @Param("fim") LocalDateTime fim);

    @Query("SELECT n.tipoNaoConformidade, COUNT(n) FROM NaoConformidadeResiduo n WHERE n.tenant.id = :tid AND n.dataHoraOcorrencia BETWEEN :ini AND :fim GROUP BY n.tipoNaoConformidade")
    List<Object[]> countByTipo(@Param("tid") Long tid, @Param("ini") LocalDateTime ini, @Param("fim") LocalDateTime fim);

    long countByTenant_IdAndStatus(Long tenantId, StatusNaoConformidade status);
}
