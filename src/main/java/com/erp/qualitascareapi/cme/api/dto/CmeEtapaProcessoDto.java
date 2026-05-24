package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.CmeEtapaTipo;

public record CmeEtapaProcessoDto(
        Long id,
        Long fluxoProcessoId,
        String codigo,
        String nome,
        CmeEtapaTipo tipoEtapa,
        Integer ordem,
        boolean obrigatoria,
        boolean permitePular,
        boolean exigeEvidencia,
        boolean exigeAprovacao,
        String rotaDestino,
        String observacoes
) {}
