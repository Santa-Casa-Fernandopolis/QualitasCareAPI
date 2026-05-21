package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.EmpresaColetoraTipo;
import com.erp.qualitascareapi.pgrss.enums.LicencaAmbientalStatus;

import java.time.LocalDate;

public record EmpresaColetorDto(
        Long id,
        Long tenantId,
        String razaoSocial,
        String cnpj,
        EmpresaColetoraTipo tipo,
        String licencaNumero,
        LocalDate licencaVencimento,
        LicencaAmbientalStatus licencaStatus,
        String telefone,
        String email,
        String responsavelNome,
        String observacoes,
        Boolean ativo
) {}
