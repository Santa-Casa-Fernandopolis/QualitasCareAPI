package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record IndicadorBiologicoRequest(@NotNull Long cicloId,
                                        String loteIndicador,
                                        String incubadora,
                                        LocalDate leituraEm,
                                        @NotNull ResultadoConformidade resultado,
                                        String observacoes,
                                        Set<Long> evidenciasIds) {
}
