package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TipoResiduoRequest(
        @NotNull Long tenantId,
        @NotNull Long grupoResiduoId,
        @NotBlank String codigo,
        @NotBlank String nome,
        String descricao,
        @NotNull PericulosidadeResiduo periculosidade,
        EstadoFisicoResiduo estadoFisico,
        TipoAcondicionamento tipoAcondicionamento,
        TipoTratamento tipoTratamento,
        TipoDestinacaoFinal tipoDestinacaoFinal,
        Boolean requerLicenca
) {}
