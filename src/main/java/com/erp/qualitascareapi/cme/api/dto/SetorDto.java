package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.core.enums.TipoSetor;

public record SetorDto(Long id, Long tenantId, String nome, TipoSetor tipo, String descricao) {
}
