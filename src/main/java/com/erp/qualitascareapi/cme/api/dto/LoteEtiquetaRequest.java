package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.LoteStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LoteEtiquetaRequest(@NotNull Long tenantId,
                                  @NotBlank String codigo,
                                  Long kitVersaoId,
                                  LocalDate dataEmpacotamento,
                                  LocalDate validade,
                                  LoteStatus status,
                                  String qrCode,
                                  Long montadoPorId,
                                  String observacoes) {
}
