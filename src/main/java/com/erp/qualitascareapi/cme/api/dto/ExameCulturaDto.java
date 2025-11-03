package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ExameCulturaResultado;

import java.time.LocalDate;
import java.util.Set;

public record ExameCulturaDto(Long id, Long tenantId, String origemAmostra, LocalDate dataColeta,
                              String responsavelColeta, ExameCulturaResultado resultado,
                              Long registradoPorId, String observacoes, Set<Long> evidenciasIds) {
}
