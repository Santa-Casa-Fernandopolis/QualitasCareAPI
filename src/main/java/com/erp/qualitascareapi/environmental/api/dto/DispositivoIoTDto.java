package com.erp.qualitascareapi.environmental.api.dto;

import com.erp.qualitascareapi.environmental.enums.TipoDispositivoIoT;

import java.time.LocalDateTime;

public record DispositivoIoTDto(
        Long id,
        Long tenantId,
        String deviceId,
        TipoDispositivoIoT tipo,
        /** Chave de API — retornada sempre para facilitar gerenciamento pelo admin. */
        String apiKey,
        Long geladeiraId,
        String geladeiraNome,
        Long ambienteId,
        String ambienteNome,
        boolean ativo,
        String descricao,
        String localInstalacao,
        LocalDateTime ultimaLeitura
) {}
