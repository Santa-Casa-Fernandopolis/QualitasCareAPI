package com.erp.qualitascareapi.environmental.api.dto;

import com.erp.qualitascareapi.environmental.enums.ResultadoMonitoramento;
import com.erp.qualitascareapi.environmental.enums.TipoAmbiente;

import java.time.LocalDateTime;
import java.util.Set;

public record MonitoramentoAmbientalDto(
        Long id,
        Long tenantId,
        LocalDateTime dataHora,
        Long ambienteId,
        String ambienteNome,
        TipoAmbiente tipoAmbiente,
        String localSala,
        Double temperaturaCelsius,
        Double umidadeRelativa,
        Double pressaoDiferencialPa,
        ResultadoMonitoramento resultado,
        Long responsavelId,
        String observacoes,
        Set<Long> evidenciasIds
) {}
