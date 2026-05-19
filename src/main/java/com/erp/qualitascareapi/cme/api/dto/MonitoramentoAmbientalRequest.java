package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoMonitoramento;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public record MonitoramentoAmbientalRequest(
        @NotNull Long tenantId,
        @NotNull LocalDateTime dataHora,
        String localSala,
        Double temperaturaCelsius,
        Double umidadeRelativa,
        Double pressaoDiferencialPa,
        @NotNull ResultadoMonitoramento resultado,
        Long responsavelId,
        String observacoes,
        Set<Long> evidenciasIds
) {}
