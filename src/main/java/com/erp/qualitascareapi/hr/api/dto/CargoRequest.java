package com.erp.qualitascareapi.hr.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CargoRequest(
        @NotNull(message = "Informe o tenant do cargo") Long tenantId,
        @NotBlank(message = "Informe o código do cargo")
        @Size(max = 60, message = "O código do cargo deve ter no máximo 60 caracteres")
        String codigo,
        @NotBlank(message = "Informe o nome do cargo")
        @Size(max = 120, message = "O nome do cargo deve ter no máximo 120 caracteres")
        String nome,
        String descricao
) {
}
