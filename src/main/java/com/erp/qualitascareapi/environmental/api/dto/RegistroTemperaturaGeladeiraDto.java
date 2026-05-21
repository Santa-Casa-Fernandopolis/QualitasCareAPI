package com.erp.qualitascareapi.environmental.api.dto;

import com.erp.qualitascareapi.environmental.enums.ResultadoMonitoramento;

import java.time.LocalDateTime;

public record RegistroTemperaturaGeladeiraDto(
        Long id,
        Long tenantId,
        Long geladeiraId,
        String geladeiraNome,
        LocalDateTime dataHora,
        Double temperaturaCelsius,
        Double umidadeRelativa,
        ResultadoMonitoramento resultado,
        String acaoCorretiva,
        Long responsavelId,
        String observacoes
) {}
