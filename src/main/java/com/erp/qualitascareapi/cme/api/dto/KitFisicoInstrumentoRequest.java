package com.erp.qualitascareapi.cme.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record KitFisicoInstrumentoRequest(
        @NotNull Long kitFisicoId,
        @NotNull Long instrumentoFisicoId,
        LocalDate vinculadoEm,
        String observacoes
) {}
