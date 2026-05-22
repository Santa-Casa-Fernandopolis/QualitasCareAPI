package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.TipoAcondicionamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TipoResiduoRequest(
        @NotNull Long tenantId,
        @NotNull Long grupoId,
        @NotBlank String nome,
        String descricao,
        TipoAcondicionamento tipoAcondicionamento,
        Boolean requerIdentificacao,
        Boolean requerPesagem
) {}
