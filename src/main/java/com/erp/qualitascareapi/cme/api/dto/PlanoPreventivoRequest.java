package com.erp.qualitascareapi.cme.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PlanoPreventivoRequest(@NotNull Long autoclaveId,
                                     Integer periodicidadeDias,
                                     Integer limiteCiclos,
                                     LocalDate proximaExecucaoPrevista,
                                     String descricao) {
}
