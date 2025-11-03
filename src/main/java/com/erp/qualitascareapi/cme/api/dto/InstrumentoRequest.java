package com.erp.qualitascareapi.cme.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InstrumentoRequest(@NotNull Long tenantId,
                                 @NotBlank String nome,
                                 String codigoHospitalar,
                                 String descricao) {
}
