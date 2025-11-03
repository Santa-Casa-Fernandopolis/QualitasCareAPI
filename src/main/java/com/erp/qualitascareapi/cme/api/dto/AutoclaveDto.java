package com.erp.qualitascareapi.cme.api.dto;

import java.time.LocalDate;

public record AutoclaveDto(Long id, Long tenantId, String nome, String fabricante, String modelo,
                           String numeroSerie, String localizacao, LocalDate ultimaHigienizacaoProfunda,
                           Boolean ativo) {
}
