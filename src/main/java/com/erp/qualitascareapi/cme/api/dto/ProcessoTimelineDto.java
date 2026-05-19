package com.erp.qualitascareapi.cme.api.dto;

import java.util.List;

public record ProcessoTimelineDto(
        ProcessoReprocessamentoDto processo,
        RecebimentoMaterialDto recebimento,
        List<LimpezaManualDto> limpezasManual,
        List<HigienizacaoUltrassonicaDto> ultrassonicas,
        List<LoteEtiquetaDto> lotes,
        List<CicloEsterilizacaoDto> ciclosEsterilizacao
) {}
