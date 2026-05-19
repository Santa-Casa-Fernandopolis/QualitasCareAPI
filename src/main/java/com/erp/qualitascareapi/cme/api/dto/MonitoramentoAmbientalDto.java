package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoMonitoramento;

import java.time.LocalDateTime;
import java.util.Set;

public record MonitoramentoAmbientalDto(
        Long id,
        Long tenantId,
        LocalDateTime dataHora,
        String localSala,
        Double temperaturaCelsius,
        Double umidadeRelativa,
        Double pressaoDiferencialPa,
        ResultadoMonitoramento resultado,
        Long responsavelId,
        String observacoes,
        Set<Long> evidenciasIds
) {}
