package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.MovimentacaoTipo;

import java.time.LocalDateTime;

/**
 * Representação pública de uma movimentação de kit/lote no CME.
 *
 * <p>Quando {@code tipo = DISPENSACAO} vinculada a uma cirurgia, os campos
 * {@code cirurgiaId}, {@code nomePaciente}, {@code tipoCirurgia} e
 * {@code dataHoraInicioCirurgia} são preenchidos para exibição direta,
 * dispensando chamadas adicionais ao módulo de integração MV.</p>
 */
public record MovimentacaoDto(
        Long id,
        Long tenantId,
        Long loteId,
        Long setorOrigemId,
        Long setorDestinoId,
        MovimentacaoTipo tipo,
        LocalDateTime dataHora,
        Long responsavelId,
        String observacoes,

        // ── Vínculo com cirurgia agendada (MV) ──────────────────────────────
        /** ID local da cirurgia agendada. Null quando não vinculada. */
        Long cirurgiaId,

        /** Código do paciente no MV — denormalizado para exibição. */
        String codigoPaciente,

        /** Nome do paciente — denormalizado para exibição. */
        String nomePaciente,

        /** Tipo/procedimento cirúrgico — denormalizado para exibição. */
        String tipoCirurgia,

        /** Sala cirúrgica — denormalizado para exibição. */
        String salaCirurgica,

        /** Data/hora de início da cirurgia — denormalizado para exibição. */
        LocalDateTime dataHoraInicioCirurgia
) {}
