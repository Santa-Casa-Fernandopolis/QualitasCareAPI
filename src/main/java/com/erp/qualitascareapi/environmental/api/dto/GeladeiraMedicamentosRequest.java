package com.erp.qualitascareapi.environmental.api.dto;

import com.erp.qualitascareapi.environmental.enums.TipoUsoGeladeira;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GeladeiraMedicamentosRequest(
        @NotNull Long tenantId,
        @NotBlank String nome,
        @NotNull TipoUsoGeladeira tipoUso,
        String localSala,
        String fabricante,
        String modelo,
        String numeroSerie,
        Double temperaturaMinCelsius,
        Double temperaturaMaxCelsius,
        Integer frequenciaLeituraHoras,
        Boolean ativo,
        String observacoes
) {}
