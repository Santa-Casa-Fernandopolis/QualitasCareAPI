package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.StatusAprovacaoCme;
import jakarta.validation.constraints.NotNull;

public record KitVersionApprovalRequest(
        @NotNull StatusAprovacaoCme status,
        Long aprovadoPorId,
        String comentario
) {}
