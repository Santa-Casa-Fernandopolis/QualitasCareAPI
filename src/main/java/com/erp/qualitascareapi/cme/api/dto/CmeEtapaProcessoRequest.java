package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.CmeEtapaTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CmeEtapaProcessoRequest(
        @NotBlank String codigo,
        @NotBlank String nome,
        @NotNull CmeEtapaTipo tipoEtapa,
        @NotNull Integer ordem,
        Boolean obrigatoria,
        Boolean permitePular,
        Boolean exigeEvidencia,
        Boolean exigeAprovacao,
        String rotaDestino,
        String observacoes
) {}
