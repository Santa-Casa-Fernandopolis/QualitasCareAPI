package com.erp.qualitascareapi.cme.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CmeFlowDefRequest(
        @NotNull Long tenantId,
        @NotBlank String nome,
        @NotBlank String tipoRecurso,
        Boolean requerAssinaturaFisica,
        Boolean ativo
) {}
