package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.GrupoResiduoCodigo;

public record GrupoResiduoDto(
        Long id,
        Long tenantId,
        GrupoResiduoCodigo codigo,
        String nome,
        String descricao,
        String corIdentificacao,
        Boolean ativo
) {}
