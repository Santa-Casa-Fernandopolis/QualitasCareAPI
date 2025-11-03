package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;

import java.time.LocalDate;
import java.util.Set;

public record IndicadorBiologicoDto(Long id, Long cicloId, String loteIndicador, String incubadora,
                                    LocalDate leituraEm, ResultadoConformidade resultado,
                                    String observacoes, Set<Long> evidenciasIds) {
}
