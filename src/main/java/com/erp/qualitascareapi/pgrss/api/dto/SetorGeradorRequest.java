package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.TipoSetorGerador;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SetorGeradorRequest(
        @NotNull Long tenantId,
        @NotBlank String nome,
        String centroCusto,
        @NotNull TipoSetorGerador tipo,
        String observacoes
) {}
