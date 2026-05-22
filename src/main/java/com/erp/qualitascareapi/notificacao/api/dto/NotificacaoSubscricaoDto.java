package com.erp.qualitascareapi.notificacao.api.dto;

import com.erp.qualitascareapi.notificacao.enums.TipoNotificacao;

import java.time.LocalDateTime;

/**
 * Projeção de leitura de {@code NotificacaoSubscricao}.
 *
 * @param id          identificador da subscrição
 * @param usuarioId   usuário assinante
 * @param tipo        tipo de notificação assinado
 * @param canalEmail  se {@code true}, o usuário também recebe e-mail
 * @param criadoEm   data de criação da assinatura
 */
public record NotificacaoSubscricaoDto(
        Long id,
        Long usuarioId,
        TipoNotificacao tipo,
        boolean canalEmail,
        LocalDateTime criadoEm
) {}
