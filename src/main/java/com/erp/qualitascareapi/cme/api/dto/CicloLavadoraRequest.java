package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoCicloLavadora;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public record CicloLavadoraRequest(
        @NotNull Long tenantId,
        @NotNull LocalDateTime dataHora,
        String equipamentoDescricao,
        String numeroCiclo,
        Double temperaturaMaxima,
        Integer duracaoMinutos,
        Integer quantidadeItens,
        @NotNull ResultadoCicloLavadora resultado,
        Long responsavelId,
        String observacoes,
        Set<Long> evidenciasIds
) {}
