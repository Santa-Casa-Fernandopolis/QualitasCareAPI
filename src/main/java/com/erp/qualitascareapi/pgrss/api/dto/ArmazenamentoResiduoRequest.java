package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.TipoArmazenamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ArmazenamentoResiduoRequest(
        @NotNull Long tenantId,
        @NotNull Long grupoId,
        @NotNull TipoArmazenamento tipoArmazenamento,
        @NotNull LocalDateTime dataHoraEntrada,
        BigDecimal pesoEstimadoKg,
        @NotBlank String responsavelNome,
        String localizacao,
        String observacoes
) {}
