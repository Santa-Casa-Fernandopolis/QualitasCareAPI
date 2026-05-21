package com.erp.qualitascareapi.notificacao.api.dto;

import com.erp.qualitascareapi.notificacao.enums.TipoNotificacao;
import jakarta.validation.constraints.NotNull;

/**
 * Payload para criar ou atualizar uma subscrição individual.
 *
 * @param tipo        tipo de notificação a assinar
 * @param canalEmail  {@code true} para receber também por e-mail (default {@code true})
 */
public record NotificacaoSubscricaoRequest(
        @NotNull TipoNotificacao tipo,
        Boolean canalEmail
) {}
