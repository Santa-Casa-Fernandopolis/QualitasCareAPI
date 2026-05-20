package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.CicloStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CicloEsterilizacaoRequest(@NotNull Long tenantId,
                                        @NotNull Long autoclaveId,
                                        Long processoId,
                                        List<Long> loteIds,
                                        @NotNull LocalDateTime inicio,
                                        LocalDateTime fim,
                                        Integer duracaoMinutos,
                                        Double temperaturaMaxima,
                                        Double pressaoMaxima,
                                        CicloStatus status,
                                        Long liberadoPorId,
                                        String observacoes) {
}
