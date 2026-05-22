package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.RiscoResiduo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GrupoResiduoRequest(
        @NotNull Long tenantId,
        @NotBlank String codigo,
        @NotBlank String nome,
        String descricao,
        @NotNull RiscoResiduo risco,
        String padraoCorIdentificacao,
        Boolean requerTratamento
) {}
