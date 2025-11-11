package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.iam.enums.TipoSetor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SetorRequest(@NotNull Long tenantId,
                           @NotBlank String nome,
                           @NotNull TipoSetor tipo,
                           String descricao) {
}
