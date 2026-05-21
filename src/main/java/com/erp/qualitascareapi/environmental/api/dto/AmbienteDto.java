package com.erp.qualitascareapi.environmental.api.dto;

import com.erp.qualitascareapi.environmental.enums.TipoAmbiente;

public record AmbienteDto(
        Long id,
        Long tenantId,
        String nome,
        TipoAmbiente tipoAmbiente,
        String bloco,
        String andar,
        String setor,
        Double temperaturaMinCelsius,
        Double temperaturaMaxCelsius,
        Double umidadeMinPercentual,
        Double umidadeMaxPercentual,
        Double pressaoMinPa,
        Double pressaoMaxPa,
        boolean ativo,
        String observacoes
) {}
