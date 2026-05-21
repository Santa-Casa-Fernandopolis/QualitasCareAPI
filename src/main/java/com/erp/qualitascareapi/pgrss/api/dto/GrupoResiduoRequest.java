package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.GrupoResiduoCodigo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GrupoResiduoRequest(
        @NotNull Long tenantId,
        @NotNull GrupoResiduoCodigo codigo,
        @NotBlank String nome,
        String descricao,
        String corIdentificacao
) {}
