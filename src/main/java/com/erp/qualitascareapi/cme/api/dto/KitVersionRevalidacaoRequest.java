package com.erp.qualitascareapi.cme.api.dto;

import jakarta.validation.constraints.NotNull;

public record KitVersionRevalidacaoRequest(
        @NotNull Integer validadeDias,
        String observacoes
) {}
