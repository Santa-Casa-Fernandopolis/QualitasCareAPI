package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.CmeEtapaExecucaoStatus;
import com.erp.qualitascareapi.cme.enums.CmeEtapaTipo;

import java.time.LocalDateTime;

public record CmeEtapaExecucaoDto(
        Long id,
        Long processoId,
        Long etapaId,
        String etapaNome,
        CmeEtapaTipo tipoEtapa,
        Integer ordem,
        CmeEtapaExecucaoStatus status,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        Long responsavelId,
        String justificativa,
        String observacoes
) {}
