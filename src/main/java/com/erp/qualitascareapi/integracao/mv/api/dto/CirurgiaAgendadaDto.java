package com.erp.qualitascareapi.integracao.mv.api.dto;

import com.erp.qualitascareapi.integracao.mv.enums.StatusCirurgiaMv;

import java.time.LocalDateTime;

/**
 * Representação pública de uma cirurgia agendada importada do Soul MV.
 */
public record CirurgiaAgendadaDto(
        Long id,
        Long tenantId,
        String idMv,
        String codigoPaciente,
        String nomePaciente,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFimPrevista,
        String tipoCirurgia,
        String salaCirurgica,
        String nomeCirurgiao,
        StatusCirurgiaMv statusMv,
        LocalDateTime ultimaSincronizacao
) {}
