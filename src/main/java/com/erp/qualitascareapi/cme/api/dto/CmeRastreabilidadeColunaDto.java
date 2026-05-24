package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.CmeEtapaTipo;

import java.util.List;

public record CmeRastreabilidadeColunaDto(
        Long etapaId,
        String codigo,
        String nome,
        CmeEtapaTipo tipoEtapa,
        Integer ordem,
        boolean obrigatoria,
        boolean permitePular,
        List<CmeRastreabilidadeCardDto> processos
) {}
