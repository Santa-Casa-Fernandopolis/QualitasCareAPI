package com.erp.qualitascareapi.environmental.api.dto;

import com.erp.qualitascareapi.environmental.enums.TipoAmbiente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AmbienteRequest(
        @NotNull Long tenantId,
        @NotBlank String nome,
        @NotNull TipoAmbiente tipoAmbiente,
        String bloco,
        String andar,
        String setor,
        Double temperaturaMinCelsius,
        Double temperaturaMaxCelsius,
        Double umidadeMinPercentual,
        Double umidadeMaxPercentual,
        Double pressaoMinPa,
        Double pressaoMaxPa,
        Boolean ativo,
        String observacoes
) {}
