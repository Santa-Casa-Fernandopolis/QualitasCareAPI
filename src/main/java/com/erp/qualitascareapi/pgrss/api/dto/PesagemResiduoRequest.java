package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.TurnoColeta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PesagemResiduoRequest(
        @NotNull Long tenantId,
        @NotNull Long setorId,
        @NotNull Long tipoId,
        @NotNull Long grupoId,
        @NotNull LocalDateTime dataHoraPesagem,
        @NotNull @DecimalMin("0.001") BigDecimal pesoKg,
        @NotNull TurnoColeta turno,
        String rota,
        @NotBlank String responsavelNome,
        String identificacaoBalanca,
        String observacoes
) {}
