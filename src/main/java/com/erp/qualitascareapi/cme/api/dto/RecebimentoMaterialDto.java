package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.RecebimentoStatus;

import java.time.LocalDateTime;
import java.util.Set;

public record RecebimentoMaterialDto(
        Long id,
        Long tenantId,
        LocalDateTime dataHora,
        Long setorOrigemId,
        Long responsavelId,
        Integer quantidadeItens,
        String condicaoDescricao,
        RecebimentoStatus status,
        String observacoes,
        Set<Long> evidenciasIds
) {}
