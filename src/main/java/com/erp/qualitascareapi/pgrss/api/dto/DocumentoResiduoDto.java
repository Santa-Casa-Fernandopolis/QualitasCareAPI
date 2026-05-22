package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.TipoDocumentoResiduo;

import java.time.LocalDateTime;

public record DocumentoResiduoDto(
        Long id,
        Long coletaExternaId,
        TipoDocumentoResiduo tipoDocumento,
        String nomeArquivo,
        String mimeType,
        Long tamanhoBytes,
        String uploadadoPorNome,
        LocalDateTime uploadadoEm
) {}
