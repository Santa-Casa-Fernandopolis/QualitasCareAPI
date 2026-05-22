package com.erp.qualitascareapi.pgrss.repo;

import com.erp.qualitascareapi.pgrss.domain.PesagemResiduo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PesagemResiduoRepository extends JpaRepository<PesagemResiduo, Long> {

    Page<PesagemResiduo> findAllByTenant_Id(Long tenantId, Pageable pageable);

    Page<PesagemResiduo> findAllByTenant_IdAndSetor_IdAndDataHoraPesagemBetween(
            Long tenantId, Long setorId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    @Query("""
            SELECT p FROM PesagemResiduo p
            WHERE p.tenant.id = :tenantId
              AND (:setorId IS NULL OR p.setor.id = :setorId)
              AND (:grupoId IS NULL OR p.grupo.id = :grupoId)
              AND (:turno IS NULL OR p.turno = :turno)
              AND (:status IS NULL OR p.status = :status)
              AND (:dataInicio IS NULL OR p.dataHoraPesagem >= :dataInicio)
              AND (:dataFim IS NULL OR p.dataHoraPesagem <= :dataFim)
            ORDER BY p.dataHoraPesagem DESC
            """)
    Page<PesagemResiduo> search(
            @Param("tenantId") Long tenantId,
            @Param("setorId") Long setorId,
            @Param("grupoId") Long grupoId,
            @Param("turno") com.erp.qualitascareapi.pgrss.enums.TurnoColeta turno,
            @Param("status") com.erp.qualitascareapi.pgrss.enums.StatusPesagem status,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable);

    @Query("SELECT SUM(p.pesoKg) FROM PesagemResiduo p WHERE p.tenant.id = :tid AND p.dataHoraPesagem BETWEEN :ini AND :fim AND p.status <> 'CANCELADA'")
    BigDecimal sumPesoPeriodo(@Param("tid") Long tid, @Param("ini") LocalDateTime ini, @Param("fim") LocalDateTime fim);

    @Query("SELECT p.grupo.codigo, SUM(p.pesoKg) FROM PesagemResiduo p WHERE p.tenant.id = :tid AND p.dataHoraPesagem BETWEEN :ini AND :fim AND p.status <> 'CANCELADA' GROUP BY p.grupo.codigo")
    List<Object[]> pesoPorGrupo(@Param("tid") Long tid, @Param("ini") LocalDateTime ini, @Param("fim") LocalDateTime fim);

    @Query("SELECT p.setor.nome, SUM(p.pesoKg) FROM PesagemResiduo p WHERE p.tenant.id = :tid AND p.dataHoraPesagem BETWEEN :ini AND :fim AND p.status <> 'CANCELADA' GROUP BY p.setor.nome ORDER BY SUM(p.pesoKg) DESC")
    List<Object[]> pesoPorSetor(@Param("tid") Long tid, @Param("ini") LocalDateTime ini, @Param("fim") LocalDateTime fim);
}
