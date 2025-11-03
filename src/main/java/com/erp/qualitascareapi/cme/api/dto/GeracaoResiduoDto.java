package com.erp.qualitascareapi.cme.api.dto;

import java.time.LocalDate;

public record GeracaoResiduoDto(Long id, Long tenantId, LocalDate dataRegistro, String tipoResiduo,
                                Double pesoEstimadoKg, String destinoFinal, Long loteId, Long saneanteId,
                                String observacoes) {
}
