package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.SeveridadeNaoConformidade;
import com.erp.qualitascareapi.pgrss.enums.StatusNaoConformidade;
import com.erp.qualitascareapi.pgrss.enums.TipoNaoConformidade;

import java.time.LocalDateTime;

public record NaoConformidadeResiduoDto(
        Long id,
        Long tenantId,
        Long setorId,
        String setorNome,
        Long grupoId,
        String grupoCodigo,
        Long tipoId,
        String tipoNome,
        LocalDateTime dataHoraOcorrencia,
        TipoNaoConformidade tipoNaoConformidade,
        SeveridadeNaoConformidade severidade,
        String descricao,
        String acaoImediata,
        String areaResponsavel,
        Boolean exigePlanoAcao,
        StatusNaoConformidade status,
        String criadoPorNome
) {}
