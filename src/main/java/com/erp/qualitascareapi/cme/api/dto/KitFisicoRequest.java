package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.IdentificacaoFisicaStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record KitFisicoRequest(
        @NotNull Long tenantId,
        Long kitId,
        Long kitVersaoAtualId,
        @NotBlank String identificadorUnico,
        IdentificacaoFisicaStatus status,
        String localizacao,
        String observacoes,
        Boolean ativo
) {}
