package com.erp.qualitascareapi.pgrss.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PlanoAcaoResiduoRequest(
        @NotBlank String descricaoAcao,
        @NotBlank String responsavelNome,
        @NotNull LocalDate dataPrazo
) {}
