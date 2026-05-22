package com.erp.qualitascareapi.pgrss.api.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record PgrssDashboardDto(
        BigDecimal pesoTotalMesKg,
        Map<String, BigDecimal> pesoPorGrupoMes,
        List<SetorPesoDto> top10SetoresGeradores,
        long naoConformidadesAbertas,
        long planosAcaoVencidos,
        long empresasLicencaVencida,
        long empresasLicencaVencendo30Dias,
        long coletasExternasSemDocumento,
        List<MesPesoDto> tendenciaMensal,
        double taxaResiduoInfectanteMes
) {}
