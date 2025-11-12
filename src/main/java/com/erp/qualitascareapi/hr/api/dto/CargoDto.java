package com.erp.qualitascareapi.hr.api.dto;

public record CargoDto(
        Long id,
        Long tenantId,
        String tenantNome,
        String codigo,
        String nome,
        String descricao
) {
}
