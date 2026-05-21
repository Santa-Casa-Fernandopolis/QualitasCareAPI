package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import com.erp.qualitascareapi.cme.enums.TipoSecagem;

import java.time.LocalDateTime;
import java.util.Set;

public record SecagemMaterialDto(
        Long id,
        Long tenantId,
        Long processoId,
        TipoSecagem tipoSecagem,
        Long responsavelId,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String equipamentoDescricao,
        ResultadoConformidade conformidade,
        String observacoes,
        Set<Long> evidenciasIds
) {}
