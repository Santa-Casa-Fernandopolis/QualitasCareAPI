package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.UsoSaneanteEtapa;

import java.time.LocalDate;

public record UsoSaneanteDto(Long id, Long loteSaneanteId, LocalDate dataUso, UsoSaneanteEtapa etapa,
                             Double volumeUtilizadoMl, String diluicao, Long usadoPorId, String observacoes) {
}
