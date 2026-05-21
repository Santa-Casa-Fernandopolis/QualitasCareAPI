package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public record ConferenciaKitRequest(
        @NotNull Long tenantId,
        Long processoId,
        @NotNull Long responsavelId,
        @NotNull LocalDateTime dataHoraConferencia,
        @NotNull ResultadoConformidade conformidade,
        String itensNaoConformes,
        String observacoes,
        Set<Long> evidenciasIds
) {}
