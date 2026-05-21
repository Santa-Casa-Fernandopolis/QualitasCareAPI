package com.erp.qualitascareapi.environmental.api.dto;

import com.erp.qualitascareapi.environmental.enums.TipoDispositivoIoT;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DispositivoIoTRequest(
        @NotNull Long tenantId,
        @NotBlank String deviceId,
        @NotNull TipoDispositivoIoT tipo,
        /** ID da geladeira. Obrigatório quando tipo = TEMPERATURA_GELADEIRA. */
        Long geladeiraId,
        /** ID do ambiente. Obrigatório quando tipo = MONITORAMENTO_AMBIENTAL. */
        Long ambienteId,
        Boolean ativo,
        String descricao,
        String localInstalacao
) {}
