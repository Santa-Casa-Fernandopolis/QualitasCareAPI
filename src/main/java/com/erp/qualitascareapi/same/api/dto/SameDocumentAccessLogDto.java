package com.erp.qualitascareapi.same.api.dto;

import com.erp.qualitascareapi.same.enums.SameAccessAction;

import java.time.LocalDateTime;

public record SameDocumentAccessLogDto(
        Long id,
        Long clinicalDocumentId,
        Long patientMasterId,
        Long userId,
        String userName,
        SameAccessAction action,
        String ipAddress,
        String userAgent,
        LocalDateTime createdAt
) {
}
