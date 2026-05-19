package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ProcessoStatus;

import java.time.LocalDateTime;

public record ProcessoReprocessamentoDto(
        Long id,
        Long tenantId,
        String numeroProcesso,
        ProcessoStatus status,
        LocalDateTime dataAbertura,
        LocalDateTime dataConclusao,
        Long recebimentoId,
        String observacoes
) {}
