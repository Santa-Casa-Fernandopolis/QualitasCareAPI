package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.StatusAprovacaoCme;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AprovacaoCicloRequest(
        @NotNull Long tenantId,
        @NotNull Long cicloId,
        Long flowDefId,
        @NotNull StatusAprovacaoCme status,
        Long aprovadoPorId,
        LocalDateTime dataAprovacao,
        String comentario
) {}
