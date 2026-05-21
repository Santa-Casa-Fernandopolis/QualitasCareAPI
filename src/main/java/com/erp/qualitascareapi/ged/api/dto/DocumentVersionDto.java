package com.erp.qualitascareapi.ged.api.dto;

import com.erp.qualitascareapi.ged.enums.DocumentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DocumentVersionDto(
        Long id,
        Long documentId,
        String documentCodigo,
        String documentTitulo,
        Integer versaoMajor,
        Integer versaoMinor,
        String semVer,
        DocumentStatus status,
        String resumoMudancas,
        LocalDate dataVigenciaInicio,
        LocalDate dataVigenciaFim,
        Long arquivoOriginalId,
        Long arquivoPublicadoId,
        Long pdfArquivoId,
        String pdfSha256,
        LocalDateTime geradoEm,
        LocalDateTime submetidoEm,
        LocalDateTime aprovadoEm,
        LocalDateTime publicadoEm
) {
}
