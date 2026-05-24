package com.erp.qualitascareapi.iam.api.dto;

import com.erp.qualitascareapi.iam.enums.TipoSetor;

public record SetorDto(
        Long id,
        Long tenantId,
        String tenantNome,
        String nome,
        TipoSetor tipo,
        Long tipoSetorId,
        String tipoSetorNome,
        Long especialidadeId,
        String especialidadeNome,
        String descricao,
        Long supervisorId,
        String supervisorNome
) {
}
