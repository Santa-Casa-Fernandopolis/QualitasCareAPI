package com.erp.qualitascareapi.environmental.api.dto;

import com.erp.qualitascareapi.environmental.enums.ResultadoMonitoramento;

import java.time.LocalDateTime;

/**
 * Alerta individual exibido no dashboard ambiental.
 * Representa uma leitura recente com resultado ALERTA ou NAO_CONFORME.
 */
public record AlertaAmbientalDto(

        /** {@code "AMBIENTE"} para monitoramentos de sala; {@code "GELADEIRA"} para geladeiras. */
        String origem,

        /** ID local do ambiente ou geladeira. */
        Long origemId,

        /** Nome do ambiente ou geladeira. */
        String nomeLocal,

        ResultadoMonitoramento resultado,

        Double temperaturaCelsius,
        Double umidadeRelativa,
        Double pressaoDiferencialPa,

        LocalDateTime dataHora
) {}
