package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.StatusAprovacaoCme;

import java.time.LocalDateTime;

public record AprovacaoLoteInalatorioDto(
        Long id,
        Long tenantId,
        Long loteId,
        Long flowDefId,
        String tipoMaterial,
        StatusAprovacaoCme status,
        Long aprovadoPorId,
        LocalDateTime dataAprovacao,
        String comentario
) {}
