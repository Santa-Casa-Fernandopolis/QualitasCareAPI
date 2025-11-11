package com.erp.qualitascareapi.iam.api.dto;

import com.erp.qualitascareapi.iam.enums.TipoSetor;

public record SetorDto(
        Long id,
        Long tenantId,
        String tenantNome,
        String nome,
        TipoSetor tipo,
        String descricao
) {
}
