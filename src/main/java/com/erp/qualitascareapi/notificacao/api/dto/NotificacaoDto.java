package com.erp.qualitascareapi.notificacao.api.dto;

import com.erp.qualitascareapi.notificacao.enums.NivelNotificacao;
import com.erp.qualitascareapi.notificacao.enums.TipoNotificacao;

import java.time.LocalDateTime;

/**
 * Projeção de leitura de {@code Notificacao}.
 *
 * @param usuarioId quando não-nulo, indica que a notificação é pessoal (ex.: parecer GED)
 */
public record NotificacaoDto(
        Long id,
        Long tenantId,
        TipoNotificacao tipo,
        NivelNotificacao nivel,
        String titulo,
        String mensagem,
        Long referenciaId,
        String referenciaTipo,
        Long usuarioId,
        boolean lida,
        LocalDateTime dataHora,
        LocalDateTime lidaEm
) {}
