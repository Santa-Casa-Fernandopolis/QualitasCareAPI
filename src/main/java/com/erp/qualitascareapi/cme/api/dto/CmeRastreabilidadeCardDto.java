package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ProcessoStatus;
import com.erp.qualitascareapi.cme.enums.TipoFluxoCME;

import java.time.LocalDateTime;

public record CmeRastreabilidadeCardDto(
        Long processoId,
        String numeroProcesso,
        TipoFluxoCME tipoFluxo,
        ProcessoStatus status,
        Long fluxoProcessoId,
        String fluxoNome,
        Long etapaExecucaoId,
        Long etapaId,
        String etapaNome,
        Integer etapaOrdem,
        LocalDateTime dataAbertura,
        LocalDateTime dataConclusao,
        String observacoes
) {}
