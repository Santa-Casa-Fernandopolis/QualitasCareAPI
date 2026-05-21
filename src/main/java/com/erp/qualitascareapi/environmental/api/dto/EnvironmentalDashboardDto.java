package com.erp.qualitascareapi.environmental.api.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Payload do dashboard de monitoramento ambiental.
 *
 * <h3>Seções</h3>
 * <ul>
 *   <li><b>Cadastro</b> — totais de ambientes, geladeiras e dispositivos IoT ativos</li>
 *   <li><b>Status atual</b> — resultado (CONFORME / ALERTA / NAO_CONFORME / SEM_LEITURA)
 *       da <em>última leitura</em> de cada ambiente e geladeira cadastrados</li>
 *   <li><b>Leituras 24h</b> — contagem de registros nas últimas 24 h por resultado,
 *       separados por ambientes e geladeiras</li>
 *   <li><b>Alertas ativos</b> — até 20 registros recentes (24 h) com ALERTA ou NAO_CONFORME,
 *       ordenados do mais recente para o mais antigo</li>
 * </ul>
 */
public record EnvironmentalDashboardDto(

        // ── Cadastro ─────────────────────────────────────────────────────────
        long totalAmbientesAtivos,
        long totalGeladeiraAtivas,
        long totalDispositivosAtivos,

        /**
         * Dispositivos ativos sem leitura nas últimas 2 h (ou que nunca enviaram).
         * Indica sensores IoT que podem estar offline.
         */
        long totalDispositivosOffline,

        // ── Status atual por sala (última leitura de cada ambiente) ──────────
        long ambientesStatusConforme,
        long ambientesStatusAlerta,
        long ambientesStatusNaoConforme,
        /** Ambientes sem nenhum registro de monitoramento ainda. */
        long ambientesSemLeitura,

        // ── Status atual por geladeira (última leitura de cada geladeira) ────
        long geladeiraStatusConforme,
        long geladeiraStatusAlerta,
        long geladeiraStatusNaoConforme,
        /** Geladeiras sem nenhum registro de temperatura ainda. */
        long geladeirasSemLeitura,

        // ── Volume de leituras nas últimas 24 h — ambientes ─────────────────
        long monitoramentos24hConformes,
        long monitoramentos24hAlerta,
        long monitoramentos24hNaoConformes,
        long monitoramentos24hTotal,

        // ── Volume de leituras nas últimas 24 h — geladeiras ────────────────
        long registrosGeladeira24hConformes,
        long registrosGeladeira24hAlerta,
        long registrosGeladeira24hNaoConformes,
        long registrosGeladeira24hTotal,

        // ── Alertas ativos (ALERTA + NAO_CONFORME das últimas 24 h) ─────────
        List<AlertaAmbientalDto> alertasAtivos,

        LocalDateTime geradoEm
) {}
