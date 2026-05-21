package com.erp.qualitascareapi.environmental.api.dto;

import com.erp.qualitascareapi.environmental.enums.ResultadoMonitoramento;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RegistroTemperaturaGeladeiraRequest(
        @NotNull Long tenantId,
        @NotNull Long geladeiraId,
        @NotNull LocalDateTime dataHora,
        @NotNull Double temperaturaCelsius,
        Double umidadeRelativa,
        @NotNull ResultadoMonitoramento resultado,
        String acaoCorretiva,
        Long responsavelId,
        String observacoes
) {}
