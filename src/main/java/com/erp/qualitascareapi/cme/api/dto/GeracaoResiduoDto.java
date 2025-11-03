package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.environmental.enums.ClasseResiduo;

import java.time.LocalDate;

public record GeracaoResiduoDto(Long id, Long tenantId, LocalDate dataRegistro, ClasseResiduo classeResiduo,
                                Double pesoEstimadoKg, String destinoFinal, Long loteId, Long saneanteId,
                                String observacoes) {
}
