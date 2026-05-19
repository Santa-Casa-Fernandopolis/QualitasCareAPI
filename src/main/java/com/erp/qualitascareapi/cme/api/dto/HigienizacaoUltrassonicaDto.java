package com.erp.qualitascareapi.cme.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record HigienizacaoUltrassonicaDto(Long id, Long tenantId, Long processoId,
                                          LocalDate dataRealizacao,
                                          LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim,
                                          String equipamentoDescricao, Long responsavelId,
                                          String observacoes, Set<Long> evidenciasIds) {
}
