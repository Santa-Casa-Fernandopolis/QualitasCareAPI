package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.StatusAprovacaoCme;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AprovacaoLoteInalatorioRequest(
        @NotNull Long tenantId,
        @NotNull Long loteId,
        Long flowDefId,
        String tipoMaterial,
        @NotNull StatusAprovacaoCme status,
        Long aprovadoPorId,
        LocalDateTime dataAprovacao,
        String comentario
) {}
