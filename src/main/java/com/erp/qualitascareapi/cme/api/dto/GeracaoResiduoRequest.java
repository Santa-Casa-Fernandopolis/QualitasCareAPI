package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.environmental.enums.ClasseResiduo;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GeracaoResiduoRequest(@NotNull Long tenantId,
                                    @NotNull LocalDate dataRegistro,
                                    @NotNull ClasseResiduo classeResiduo,
                                    Double pesoEstimadoKg,
                                    String destinoFinal,
                                    Long loteId,
                                    Long saneanteId,
                                    String observacoes) {
}
