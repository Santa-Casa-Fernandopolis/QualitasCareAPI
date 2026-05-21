package com.erp.qualitascareapi.environmental.repo;

import com.erp.qualitascareapi.environmental.domain.RegistroTemperaturaGeladeira;
import com.erp.qualitascareapi.environmental.enums.ResultadoMonitoramento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroTemperaturaGeladeiraRepository extends JpaRepository<RegistroTemperaturaGeladeira, Long> {

    Page<RegistroTemperaturaGeladeira> findAllByTenantId(Long tenantId, Pageable pageable);

    Page<RegistroTemperaturaGeladeira> findAllByTenantIdAndGeladeiraId(
            Long tenantId, Long geladeiraId, Pageable pageable);

    // ── Dashboard ────────────────────────────────────────────────────────────

    /** Contagem de leituras por resultado num período. */
    long countByTenantIdAndResultadoAndDataHoraAfter(
            Long tenantId, ResultadoMonitoramento resultado, LocalDateTime dataHoraAfter);

    /**
     * Última leitura registrada por geladeira.
     * Usado para calcular o status atual de cada equipamento.
     */
    @Query("""
            SELECT r FROM RegistroTemperaturaGeladeira r
            WHERE r.tenant.id = :tenantId
              AND r.dataHora = (
                  SELECT MAX(r2.dataHora) FROM RegistroTemperaturaGeladeira r2
                  WHERE r2.geladeira = r.geladeira
                    AND r2.tenant.id = :tenantId
              )
            """)
    List<RegistroTemperaturaGeladeira> findUltimaLeituraPorGeladeira(@Param("tenantId") Long tenantId);

    /**
     * Registros recentes com resultado de alerta ou não-conformidade.
     */
    @Query("""
            SELECT r FROM RegistroTemperaturaGeladeira r
            WHERE r.tenant.id = :tenantId
              AND r.resultado IN :resultados
              AND r.dataHora >= :after
            ORDER BY r.dataHora DESC
            """)
    List<RegistroTemperaturaGeladeira> findAlertasRecentes(
            @Param("tenantId") Long tenantId,
            @Param("resultados") List<ResultadoMonitoramento> resultados,
            @Param("after") LocalDateTime after,
            Pageable pageable);
}
