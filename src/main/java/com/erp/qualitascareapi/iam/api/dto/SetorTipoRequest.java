package com.erp.qualitascareapi.iam.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SetorTipoRequest(
        @NotNull(message = "Informe o tenant") Long tenantId,
        @NotBlank(message = "Informe o nome")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nome,
        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String descricao,
        Boolean active
) {
}

