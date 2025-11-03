package com.erp.qualitascareapi.cme.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GeracaoResiduoRequest(@NotNull Long tenantId,
                                    @NotNull LocalDate dataRegistro,
                                    String tipoResiduo,
                                    Double pesoEstimadoKg,
                                    String destinoFinal,
                                    Long loteId,
                                    Long saneanteId,
                                    String observacoes) {
}
