package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.TipoAcondicionamento;

public record TipoResiduoDto(
        Long id,
        Long tenantId,
        Long grupoId,
        String grupoNome,
        String nome,
        String descricao,
        TipoAcondicionamento tipoAcondicionamento,
        Boolean requerIdentificacao,
        Boolean requerPesagem,
        Boolean ativo
) {}
