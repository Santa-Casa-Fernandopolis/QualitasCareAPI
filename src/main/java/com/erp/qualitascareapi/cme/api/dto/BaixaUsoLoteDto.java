package com.erp.qualitascareapi.cme.api.dto;

public record BaixaUsoLoteDto(
        LoteEtiquetaDto lote,
        MovimentacaoDto movimentacao,
        IndicadorQuimicoDto marcadorQuimicoInterno
) {}
