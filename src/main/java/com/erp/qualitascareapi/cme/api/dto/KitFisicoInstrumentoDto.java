package com.erp.qualitascareapi.cme.api.dto;

import java.time.LocalDate;

public record KitFisicoInstrumentoDto(
        Long id,
        Long kitFisicoId,
        Long instrumentoFisicoId,
        String instrumentoIdentificadorUnico,
        Long instrumentoId,
        String instrumentoNome,
        LocalDate vinculadoEm,
        LocalDate desvinculadoEm,
        String observacoes,
        Boolean ativo
) {}
