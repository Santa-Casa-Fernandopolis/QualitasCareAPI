package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.RecebimentoStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public record RecebimentoMaterialRequest(
        @NotNull Long tenantId,
        @NotNull LocalDateTime dataHora,
        Long setorOrigemId,
        Long responsavelId,
        Integer quantidadeItens,
        String condicaoDescricao,
        RecebimentoStatus status,
        String observacoes,
        Set<Long> evidenciasIds
) {}
