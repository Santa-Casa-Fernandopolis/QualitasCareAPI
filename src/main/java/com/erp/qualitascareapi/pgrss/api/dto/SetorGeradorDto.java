package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.TipoSetorGerador;

public record SetorGeradorDto(
        Long id,
        Long tenantId,
        String nome,
        String centroCusto,
        TipoSetorGerador tipo,
        Boolean ativo,
        String observacoes
) {}
