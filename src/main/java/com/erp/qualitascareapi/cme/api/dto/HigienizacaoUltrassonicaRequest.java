package com.erp.qualitascareapi.cme.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record HigienizacaoUltrassonicaRequest(@NotNull Long tenantId,
                                              @NotNull LocalDate dataRealizacao,
                                              String equipamentoDescricao,
                                              Long responsavelId,
                                              String observacoes,
                                              Set<Long> evidenciasIds) {
}
