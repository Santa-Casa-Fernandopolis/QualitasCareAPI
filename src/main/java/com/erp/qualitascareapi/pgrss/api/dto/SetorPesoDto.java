package com.erp.qualitascareapi.pgrss.api.dto;

import java.math.BigDecimal;

public record SetorPesoDto(
        String setorNome,
        BigDecimal pesoKg
) {}
