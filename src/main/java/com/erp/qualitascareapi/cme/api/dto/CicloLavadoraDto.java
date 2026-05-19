package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoCicloLavadora;

import java.time.LocalDateTime;
import java.util.Set;

public record CicloLavadoraDto(
        Long id,
        Long tenantId,
        LocalDateTime dataHora,
        String equipamentoDescricao,
        String numeroCiclo,
        Double temperaturaMaxima,
        Integer duracaoMinutos,
        Integer quantidadeItens,
        ResultadoCicloLavadora resultado,
        Long responsavelId,
        String observacoes,
        Set<Long> evidenciasIds
) {}
