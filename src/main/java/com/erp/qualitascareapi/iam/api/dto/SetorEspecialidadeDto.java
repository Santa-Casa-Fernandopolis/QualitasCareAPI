package com.erp.qualitascareapi.iam.api.dto;

public record SetorEspecialidadeDto(
        Long id,
        Long tenantId,
        String tenantNome,
        Long tipoSetorId,
        String tipoSetorNome,
        String nome,
        String descricao,
        boolean active
) {
}

