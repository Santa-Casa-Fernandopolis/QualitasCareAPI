package com.erp.qualitascareapi.environmental.api.dto;

import com.erp.qualitascareapi.environmental.enums.ResultadoMonitoramento;
import com.erp.qualitascareapi.environmental.enums.TipoDispositivoIoT;

import java.time.LocalDateTime;

/**
 * Resposta retornada ao dispositivo IoT após o processamento da leitura.
 */
public record IoTLeituraResponse(
        TipoDispositivoIoT tipoDispositivo,
        Long registroId,
        ResultadoMonitoramento resultado,
        LocalDateTime processadoEm
) {}
