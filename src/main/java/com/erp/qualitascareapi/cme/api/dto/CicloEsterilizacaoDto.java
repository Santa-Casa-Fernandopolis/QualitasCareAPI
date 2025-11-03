package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.CicloStatus;

import java.time.LocalDateTime;

public record CicloEsterilizacaoDto(Long id, Long tenantId, Long autoclaveId, Long loteId,
                                    LocalDateTime inicio, LocalDateTime fim, Integer duracaoMinutos,
                                    Double temperaturaMaxima, Double pressaoMaxima, CicloStatus status,
                                    Long liberadoPorId, String observacoes) {
}
