package com.erp.qualitascareapi.integracao.mv.strategy;

import java.time.LocalDateTime;

/**
 * DTO interno que representa uma cirurgia lida diretamente da fonte MV,
 * independentemente da estratégia usada (API REST ou banco de dados).
 *
 * <p>Ambas as implementações de {@link MvIntegracaoStrategy} devem mapear
 * seus resultados para este record antes de devolver ao serviço orquestrador.</p>
 */
public record MvCirurgiaRaw(
        /** Identificador único da cirurgia no sistema MV. */
        String idMv,

        /** Código do paciente no MV. */
        String codigoPaciente,

        String nomePaciente,

        LocalDateTime dataHoraInicio,

        LocalDateTime dataHoraFimPrevista,

        /** Tipo/descrição do procedimento cirúrgico. */
        String tipoCirurgia,

        String salaCirurgica,

        String nomeCirurgiao,

        /**
         * Status original conforme retornado pelo MV.
         * Será mapeado para {@link com.erp.qualitascareapi.integracao.mv.enums.StatusCirurgiaMv}.
         */
        String statusMv
) {}
