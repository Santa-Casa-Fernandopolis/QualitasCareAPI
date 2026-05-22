package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.SeveridadeNaoConformidade;
import com.erp.qualitascareapi.pgrss.enums.TipoNaoConformidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record NaoConformidadeResiduoRequest(
        @NotNull Long tenantId,
        Long setorId,
        @NotNull LocalDateTime dataHoraOcorrencia,
        Long grupoId,
        Long tipoId,
        @NotNull TipoNaoConformidade tipoNaoConformidade,
        @NotNull SeveridadeNaoConformidade severidade,
        @NotBlank String descricao,
        String acaoImediata,
        String areaResponsavel,
        String criadoPorNome
) {}
