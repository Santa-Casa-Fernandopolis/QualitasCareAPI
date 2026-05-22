package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.StatusAprovacaoCme;

import java.time.LocalDateTime;

public record AprovacaoCicloDto(
        Long id,
        Long tenantId,
        Long cicloId,
        Long flowDefId,
        StatusAprovacaoCme status,
        Long aprovadoPorId,
        LocalDateTime dataAprovacao,
        String comentario
) {}
