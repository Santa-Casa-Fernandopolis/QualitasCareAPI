package com.erp.qualitascareapi.cme.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record KitVersionRequest(@NotNull Long kitId,
                                @NotNull Integer numeroVersao,
                                LocalDate vigenciaInicio,
                                Integer validadeDias,
                                Boolean ativo,
                                String observacoes) {
}
