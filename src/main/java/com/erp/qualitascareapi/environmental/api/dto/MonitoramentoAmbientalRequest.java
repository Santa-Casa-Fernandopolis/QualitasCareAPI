package com.erp.qualitascareapi.environmental.api.dto;

import com.erp.qualitascareapi.environmental.enums.ResultadoMonitoramento;
import com.erp.qualitascareapi.environmental.enums.TipoAmbiente;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public record MonitoramentoAmbientalRequest(
        @NotNull Long tenantId,
        @NotNull LocalDateTime dataHora,
        TipoAmbiente tipoAmbiente,
        String localSala,
        Double temperaturaCelsius,
        Double umidadeRelativa,
        Double pressaoDiferencialPa,
        @NotNull ResultadoMonitoramento resultado,
        Long responsavelId,
        String observacoes,
        Set<Long> evidenciasIds
) {}
