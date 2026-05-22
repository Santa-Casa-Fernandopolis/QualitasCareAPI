package com.erp.qualitascareapi.cme.api.dto;

import java.util.List;
import java.util.Map;

public record CmeDashboardDto(
        // KPIs — esterilização
        double sterilizationRate,
        double turnaroundTimeMinutes,
        long pendingLoads,
        long processosAbertos,
        // KPIs — operacionais
        long recebimentosHoje,
        long ciclosHoje,
        long lotesVencendoEm30Dias,
        long manutencoesPendentes,
        // Distribuições por status
        Map<String, Long> processosPorStatus,
        Map<String, Long> ciclosPorStatus,
        Map<String, Long> lotesPorStatus,
        // Listas recentes
        List<RecebimentoMaterialDto> recebimentosRecentes
) {}
