package com.erp.qualitascareapi.environmental.api.dto;

import com.erp.qualitascareapi.environmental.enums.TipoUsoGeladeira;

public record GeladeiraMedicamentosDto(
        Long id,
        Long tenantId,
        String nome,
        TipoUsoGeladeira tipoUso,
        String localSala,
        String fabricante,
        String modelo,
        String numeroSerie,
        Double temperaturaMinCelsius,
        Double temperaturaMaxCelsius,
        Integer frequenciaLeituraHoras,
        boolean ativo,
        String observacoes
) {}
