package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.RiscoResiduo;

public record GrupoResiduoDto(
        Long id,
        Long tenantId,
        String codigo,
        String nome,
        String descricao,
        RiscoResiduo risco,
        String padraoCorIdentificacao,
        Boolean requerTratamento,
        Boolean ativo
) {}
