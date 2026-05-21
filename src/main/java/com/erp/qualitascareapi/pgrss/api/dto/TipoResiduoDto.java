package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.*;

public record TipoResiduoDto(
        Long id,
        Long tenantId,
        Long grupoResiduoId,
        String grupoResiduoNome,
        String codigo,
        String nome,
        String descricao,
        PericulosidadeResiduo periculosidade,
        EstadoFisicoResiduo estadoFisico,
        TipoAcondicionamento tipoAcondicionamento,
        TipoTratamento tipoTratamento,
        TipoDestinacaoFinal tipoDestinacaoFinal,
        Boolean requerLicenca,
        Boolean ativo
) {}
