package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.SetorGeradorTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SetorGeradorRequest(
        @NotNull Long tenantId,
        @NotBlank String nome,
        String codigoInterno,
        SetorGeradorTipo tipo,
        String descricao,
        Long setorId
) {}
