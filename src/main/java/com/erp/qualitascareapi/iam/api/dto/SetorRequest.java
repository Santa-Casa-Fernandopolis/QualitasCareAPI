package com.erp.qualitascareapi.iam.api.dto;

import com.erp.qualitascareapi.iam.enums.TipoSetor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SetorRequest(
        @NotNull(message = "Informe o tenant do setor") Long tenantId,
        @NotBlank(message = "Informe o nome do setor")
        @Size(max = 120, message = "O nome do setor deve ter no máximo 120 caracteres")
        String nome,
        @NotNull(message = "Informe o tipo do setor") TipoSetor tipo,
        @Size(max = 255, message = "A descrição do setor deve ter no máximo 255 caracteres")
        String descricao
) {
}
