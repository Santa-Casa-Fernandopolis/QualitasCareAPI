package com.erp.qualitascareapi.pgrss.api.dto;

import java.time.LocalDate;

public record EmpresaColetorDto(
        Long id,
        Long tenantId,
        String razaoSocial,
        String nomeFantasia,
        String cnpj,
        String numeroLicenca,
        LocalDate dataVencimentoLicenca,
        String nomeContato,
        String telefone,
        String email,
        Boolean ativo
) {}
