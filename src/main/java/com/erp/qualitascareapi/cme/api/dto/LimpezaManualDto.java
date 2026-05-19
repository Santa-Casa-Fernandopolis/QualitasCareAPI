package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.MetodoLimpeza;
import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;

import java.time.LocalDateTime;
import java.util.Set;

public record LimpezaManualDto(
        Long id,
        Long tenantId,
        Long processoId,
        Long responsavelId,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String produtoUtilizado,
        String concentracao,
        MetodoLimpeza metodo,
        ResultadoConformidade conformidade,
        String observacoes,
        Set<Long> evidenciasIds
) {}
