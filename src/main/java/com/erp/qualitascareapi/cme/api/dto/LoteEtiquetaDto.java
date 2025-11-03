package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.LoteStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoteEtiquetaDto(Long id, Long tenantId, String codigo, Long kitVersaoId,
                              LocalDate dataEmpacotamento, LocalDate validade,
                              LoteStatus status, String qrCode, Long montadoPorId,
                              String observacoes, LocalDateTime criadoEm) {
}
