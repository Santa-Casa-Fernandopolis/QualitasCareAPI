package com.erp.qualitascareapi.cme.api.dto;

import java.time.LocalDate;

public record SaneanteLoteDto(Long id, Long tenantId, String numeroLote, String fabricante,
                              String concentracao, LocalDate dataValidade, LocalDate dataAbertura,
                              Double volumeInicialMl, String observacoes) {
}
