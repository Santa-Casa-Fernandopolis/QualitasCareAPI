package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import com.erp.qualitascareapi.cme.enums.TipoSecagem;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public record SecagemMaterialRequest(
        @NotNull Long tenantId,
        Long processoId,
        @NotNull TipoSecagem tipoSecagem,
        @NotNull Long responsavelId,
        @NotNull LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String equipamentoDescricao,
        ResultadoConformidade conformidade,
        String observacoes,
        Set<Long> evidenciasIds
) {}
