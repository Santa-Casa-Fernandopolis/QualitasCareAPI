package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.UsoSaneanteEtapa;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UsoSaneanteRequest(@NotNull Long loteSaneanteId,
                                 @NotNull LocalDate dataUso,
                                 @NotNull UsoSaneanteEtapa etapa,
                                 Double volumeUtilizadoMl,
                                 String diluicao,
                                 Long usadoPorId,
                                 String observacoes) {
}
