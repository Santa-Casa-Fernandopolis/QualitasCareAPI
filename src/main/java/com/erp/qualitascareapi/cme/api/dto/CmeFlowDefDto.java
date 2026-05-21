package com.erp.qualitascareapi.cme.api.dto;

public record CmeFlowDefDto(
        Long id,
        Long tenantId,
        String nome,
        String tipoRecurso,
        Boolean requerAssinaturaFisica,
        Boolean ativo
) {}
