package com.erp.qualitascareapi.environmental.repo;

import com.erp.qualitascareapi.environmental.domain.MonitoramentoAmbiental;
import com.erp.qualitascareapi.environmental.enums.ResultadoMonitoramento;
import com.erp.qualitascareapi.environmental.enums.TipoAmbiente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MonitoramentoAmbientalRepository extends JpaRepository<MonitoramentoAmbiental, Long> {

    Optional<MonitoramentoAmbiental> findByIdAndTenantId(Long id, Long tenantId);

    Page<MonitoramentoAmbiental> findAllByTenantId(Long tenantId, Pageable pageable);

    Page<MonitoramentoAmbiental> findAllByTenantIdAndTipoAmbiente(
            Long tenantId, TipoAmbiente tipoAmbiente, Pageable pageable);

    // ── Dashboard ────────────────────────────────────────────────────────────

    /** Contagem de registros por resultado num período. */
    long countByTenantIdAndResultadoAndDataHoraAfter(
            Long tenantId, ResultadoMonitoramento resultado, LocalDateTime dataHoraAfter);

    /**
     * Última leitura registrada para cada ambiente vinculado.
     * Usado para calcular o status atual (CONFORME / ALERTA / NAO_CONFORME) de cada sala.
     */
    @Query("""
            SELECT m FROM MonitoramentoAmbiental m
            WHERE m.tenant.id = :tenantId
              AND m.ambiente IS NOT NULL
              AND m.dataHora = (
                  SELECT MAX(m2.dataHora) FROM MonitoramentoAmbiental m2
                  WHERE m2.ambiente = m.ambiente
                    AND m2.tenant.id = :tenantId
              )
            """)
    List<MonitoramentoAmbiental> findUltimaLeituraPorAmbiente(@Param("tenantId") Long tenantId);

    /**
     * Registros recentes com resultado de alerta ou não-conformidade,
     * ordenados do mais recente para o mais antigo.
     */
    @Query("""
            SELECT m FROM MonitoramentoAmbiental m
            WHERE m.tenant.id = :tenantId
              AND m.resultado IN :resultados
              AND m.dataHora >= :after
            ORDER BY m.dataHora DESC
            """)
    List<MonitoramentoAmbiental> findAlertasRecentes(
            @Param("tenantId") Long tenantId,
            @Param("resultados") List<ResultadoMonitoramento> resultados,
            @Param("after") LocalDateTime after,
            Pageable pageable);
}
