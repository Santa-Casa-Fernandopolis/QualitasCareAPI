package com.erp.qualitascareapi.hr.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record TurnoTrabalhoRequest(
        @NotNull Long tenantId,
        @NotBlank String codigo,
        @NotBlank String nome,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFim,
        Boolean cruzaMeiaNoite,
        @Min(0) @Max(1440) Integer intervaloMinutos,
        Boolean active,
        String descricao
) {}
