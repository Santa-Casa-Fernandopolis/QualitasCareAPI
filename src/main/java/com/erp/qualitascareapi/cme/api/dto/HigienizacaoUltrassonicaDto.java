package com.erp.qualitascareapi.cme.api.dto;

import java.time.LocalDate;
import java.util.Set;

public record HigienizacaoUltrassonicaDto(Long id, Long tenantId, LocalDate dataRealizacao,
                                          String equipamentoDescricao, Long responsavelId,
                                          String observacoes, Set<Long> evidenciasIds) {
}
