package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record IndicadorQuimicoRequest(@NotNull Long cicloId,
                                      String tipo,
                                      @NotNull ResultadoConformidade resultado,
                                      String observacoes,
                                      Set<Long> evidenciasIds) {
}
