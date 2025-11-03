package com.erp.qualitascareapi.cme.api.dto;

import jakarta.validation.constraints.NotNull;

public record KitItemRequest(@NotNull Long versaoId,
                             @NotNull Long instrumentoId,
                             @NotNull Integer quantidade,
                             String observacoes) {
}
