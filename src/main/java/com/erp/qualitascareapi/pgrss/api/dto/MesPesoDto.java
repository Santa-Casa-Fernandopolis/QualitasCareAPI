package com.erp.qualitascareapi.pgrss.api.dto;

import java.math.BigDecimal;

public record MesPesoDto(
        String mes,
        BigDecimal pesoKg
) {}
