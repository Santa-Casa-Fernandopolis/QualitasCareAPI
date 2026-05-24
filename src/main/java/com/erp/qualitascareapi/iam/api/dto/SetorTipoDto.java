package com.erp.qualitascareapi.iam.api.dto;

public record SetorTipoDto(
        Long id,
        Long tenantId,
        String tenantNome,
        String nome,
        String descricao,
        boolean active
) {
}

