package com.erp.qualitascareapi.ged.api.dto;

import java.time.LocalDate;

public record PublishDocumentRequest(
        LocalDate dataVigenciaInicio,
        LocalDate dataVigenciaFim,
        String observacoesPublicacao
) {
}
