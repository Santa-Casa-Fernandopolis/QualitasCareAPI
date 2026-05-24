package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ProcessoStatus;
import com.erp.qualitascareapi.cme.enums.TipoFluxoCME;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ProcessoReprocessamentoRequest(
        @NotNull Long tenantId,
        @NotBlank String numeroProcesso,
        TipoFluxoCME tipoFluxo,
        Long fluxoProcessoId,
        ProcessoStatus status,
        @NotNull LocalDateTime dataAbertura,
        LocalDateTime dataConclusao,
        Long recebimentoId,
        String observacoes
) {}
