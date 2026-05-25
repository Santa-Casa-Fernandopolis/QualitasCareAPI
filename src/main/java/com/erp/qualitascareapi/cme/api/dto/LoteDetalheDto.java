package com.erp.qualitascareapi.cme.api.dto;

import java.util.List;

public record LoteDetalheDto(
        LoteEtiquetaDto lote,
        KitFisicoDto kitFisico,
        List<KitFisicoInstrumentoDto> instrumentosFisicos,
        ProcessoTimelineDto processoTimeline,
        List<MovimentacaoDto> movimentacoes,
        List<IndicadorQuimicoDto> indicadoresQuimicos,
        List<IndicadorBiologicoDto> indicadoresBiologicos,
        List<TesteBowieDickDto> testesBowieDick
) {}
