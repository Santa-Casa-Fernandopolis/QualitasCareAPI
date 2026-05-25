package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.CmeEtapaTipo;

public record CmeEtapaCatalogoDto(
        Long id,
        Long tenantId,
        String codigo,
        String nome,
        CmeEtapaTipo tipoEtapa,
        boolean obrigatoria,
        boolean permitePular,
        boolean exigeEvidencia,
        boolean exigeAprovacao,
        String rotaDestino,
        boolean ativo,
        String observacoes
) {}
