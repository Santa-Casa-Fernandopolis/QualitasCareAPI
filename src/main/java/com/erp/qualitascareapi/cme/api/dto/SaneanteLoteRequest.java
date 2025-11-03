package com.erp.qualitascareapi.cme.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SaneanteLoteRequest(@NotNull Long tenantId,
                                  @NotBlank String numeroLote,
                                  String fabricante,
                                  String concentracao,
                                  LocalDate dataValidade,
                                  LocalDate dataAbertura,
                                  Double volumeInicialMl,
                                  String observacoes) {
}
