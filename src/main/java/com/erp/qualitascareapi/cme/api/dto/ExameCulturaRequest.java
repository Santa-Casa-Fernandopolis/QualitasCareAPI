package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.core.enums.ExameCulturaResultado;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record ExameCulturaRequest(@NotNull Long tenantId,
                                  String origemAmostra,
                                  @NotNull LocalDate dataColeta,
                                  String responsavelColeta,
                                  ExameCulturaResultado resultado,
                                  Long registradoPorId,
                                  String observacoes,
                                  Set<Long> evidenciasIds) {
}
