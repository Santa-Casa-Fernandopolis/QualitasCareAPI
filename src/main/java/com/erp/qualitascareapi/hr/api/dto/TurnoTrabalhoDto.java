package com.erp.qualitascareapi.hr.api.dto;

import java.time.LocalTime;

public record TurnoTrabalhoDto(
        Long id,
        Long tenantId,
        String tenantNome,
        String codigo,
        String nome,
        LocalTime horaInicio,
        LocalTime horaFim,
        boolean cruzaMeiaNoite,
        Integer intervaloMinutos,
        Integer cargaHorariaMinutos,
        boolean active,
        String descricao
) {}
