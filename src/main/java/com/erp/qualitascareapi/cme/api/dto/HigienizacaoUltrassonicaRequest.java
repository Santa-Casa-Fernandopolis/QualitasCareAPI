package com.erp.qualitascareapi.cme.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record HigienizacaoUltrassonicaRequest(@NotNull Long tenantId,
                                              Long processoId,
                                              @NotNull LocalDate dataRealizacao,
                                              LocalDateTime dataHoraInicio,
                                              LocalDateTime dataHoraFim,
                                              String equipamentoDescricao,
                                              Long responsavelId,
                                              String observacoes,
                                              Set<Long> evidenciasIds) {
}
