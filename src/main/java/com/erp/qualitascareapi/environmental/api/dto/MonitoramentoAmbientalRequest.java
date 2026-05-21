package com.erp.qualitascareapi.environmental.api.dto;

import com.erp.qualitascareapi.environmental.enums.ResultadoMonitoramento;
import com.erp.qualitascareapi.environmental.enums.TipoAmbiente;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public record MonitoramentoAmbientalRequest(
        @NotNull Long tenantId,
        @NotNull LocalDateTime dataHora,
        /** ID do ambiente cadastrado. Quando informado, tipoAmbiente e localSala são opcionais. */
        Long ambienteId,
        /** Tipo do ambiente. Ignorado se ambienteId estiver preenchido. */
        TipoAmbiente tipoAmbiente,
        String localSala,
        Double temperaturaCelsius,
        Double umidadeRelativa,
        Double pressaoDiferencialPa,
        /**
         * Resultado da medição. Se null, o sistema avalia automaticamente
         * com base nos parâmetros alvo do ambiente vinculado.
         */
        ResultadoMonitoramento resultado,
        Long responsavelId,
        String observacoes,
        Set<Long> evidenciasIds
) {}
