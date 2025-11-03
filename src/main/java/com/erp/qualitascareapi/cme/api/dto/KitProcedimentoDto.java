package com.erp.qualitascareapi.cme.api.dto;

public record KitProcedimentoDto(Long id, Long tenantId, String nome, String codigo, String observacoes, Boolean ativo) {
}
