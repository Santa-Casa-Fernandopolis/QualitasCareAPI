package com.erp.qualitascareapi.ged.api.dto;

import com.erp.qualitascareapi.ged.enums.DocumentSignatureStatus;

import java.time.LocalDateTime;

public record DocumentSignatureDto(
        Long id,
        Long documentVersionId,
        Long signerId,
        String signerName,
        String roleLabel,
        DocumentSignatureStatus status,
        LocalDateTime requestedAt,
        LocalDateTime signedAt,
        String comment
) {
}
