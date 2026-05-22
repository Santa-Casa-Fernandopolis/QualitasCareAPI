package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.MovimentacaoTipo;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Payload para registrar uma movimentação de kit/lote no CME.
 *
 * <p>Para vincular a retirada (tipo {@code DISPENSACAO}) a uma cirurgia agendada do MV,
 * informe o {@code cirurgiaId} retornado por {@code GET /api/mv/cirurgias}.
 * O vínculo é opcional — dispensações sem cirurgia continuam sendo permitidas.</p>
 */
public record MovimentacaoRequest(
        @NotNull Long tenantId,
        Long loteId,
        Long setorOrigemId,
        Long setorDestinoId,
        @NotNull MovimentacaoTipo tipo,
        @NotNull LocalDateTime dataHora,
        Long responsavelId,
        String observacoes,

        /**
         * ID da cirurgia agendada (tabela {@code cme_cirurgias_agendadas}).
         * Opcional — informe apenas quando {@code tipo = DISPENSACAO} e a
         * retirada estiver associada a um procedimento cirúrgico específico.
         */
        Long cirurgiaId
) {}
