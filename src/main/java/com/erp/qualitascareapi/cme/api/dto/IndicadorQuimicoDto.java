package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;

import java.util.Set;

public record IndicadorQuimicoDto(Long id, Long cicloId, String tipo, ResultadoConformidade resultado,
                                  String observacoes, Set<Long> evidenciasIds) {
}
