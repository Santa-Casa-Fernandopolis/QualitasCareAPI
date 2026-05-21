package com.erp.qualitascareapi.notificacao.api.dto;

import com.erp.qualitascareapi.notificacao.enums.NivelNotificacao;
import com.erp.qualitascareapi.notificacao.enums.TipoNotificacao;

import java.time.LocalDateTime;

/**
 * Projeção de leitura de {@code Notificacao} retornada pela API.
 *
 * @param id            identificador único
 * @param tenantId      tenant ao qual a notificação pertence
 * @param tipo          tipo semântico da notificação
 * @param nivel         nível de severidade (INFO, ALERTA, CRITICO)
 * @param titulo        título resumido (max 200 chars)
 * @param mensagem      descrição detalhada (max 500 chars)
 * @param referenciaId  ID do registro de origem (pode ser null)
 * @param referenciaTipo tipo do registro de origem: "GELADEIRA", "AMBIENTE", "IOT"
 * @param lida          se o usuário já marcou como lida
 * @param dataHora      momento em que a notificação foi gerada
 * @param lidaEm        momento em que foi marcada como lida (null se ainda não lida)
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
        boolean lida,
        LocalDateTime dataHora,
        LocalDateTime lidaEm
) {}
