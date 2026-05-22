package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;

import java.time.LocalDateTime;
import java.util.Set;

public record ConferenciaKitDto(
        Long id,
        Long tenantId,
        Long processoId,
        Long responsavelId,
        LocalDateTime dataHoraConferencia,
        ResultadoConformidade conformidade,
        String itensNaoConformes,
        String observacoes,
        Set<Long> evidenciasIds
) {}
