package com.erp.qualitascareapi.cme.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EntradaKitCmeRequest(@NotNull Long tenantId,
                                   @NotNull Long kitFisicoId,
                                   Long responsavelId,
                                   LocalDateTime entradaEm,
                                   String observacoes) {
}
