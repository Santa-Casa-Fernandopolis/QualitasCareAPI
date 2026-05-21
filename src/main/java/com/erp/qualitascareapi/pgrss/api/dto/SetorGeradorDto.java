package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.SetorGeradorTipo;

public record SetorGeradorDto(
        Long id,
        Long tenantId,
        Long setorId,
        String nome,
        String codigoInterno,
        SetorGeradorTipo tipo,
        String descricao,
        Boolean ativo
) {}
