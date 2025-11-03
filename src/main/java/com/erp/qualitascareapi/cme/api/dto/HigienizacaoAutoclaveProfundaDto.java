package com.erp.qualitascareapi.cme.api.dto;

import java.time.LocalDate;
import java.util.Set;

public record HigienizacaoAutoclaveProfundaDto(Long id, Long autoclaveId, LocalDate dataRealizacao,
                                               Long responsavelId, String observacoes, Set<Long> evidenciasIds) {
}
