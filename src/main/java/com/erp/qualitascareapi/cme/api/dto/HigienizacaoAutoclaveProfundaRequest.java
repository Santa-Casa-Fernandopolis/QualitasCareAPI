package com.erp.qualitascareapi.cme.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record HigienizacaoAutoclaveProfundaRequest(@NotNull Long autoclaveId,
                                                   @NotNull LocalDate dataRealizacao,
                                                   Long responsavelId,
                                                   String observacoes,
                                                   Set<Long> evidenciasIds) {
}
