package com.erp.qualitascareapi.cme.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record KitProcedimentoRequest(@NotNull Long tenantId,
                                     @NotBlank String nome,
                                     String codigo,
                                     String observacoes,
                                     Boolean ativo) {
}
