package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.MetodoLimpeza;
import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public record LimpezaManualRequest(
        @NotNull Long tenantId,
        Long processoId,
        @NotNull Long responsavelId,
        @NotNull LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String produtoUtilizado,
        String concentracao,
        MetodoLimpeza metodo,
        ResultadoConformidade conformidade,
        String observacoes,
        Set<Long> evidenciasIds
) {}
