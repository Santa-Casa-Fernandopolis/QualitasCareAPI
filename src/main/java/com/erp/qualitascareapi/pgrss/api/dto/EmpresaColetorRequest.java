package com.erp.qualitascareapi.pgrss.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EmpresaColetorRequest(
        @NotNull Long tenantId,
        @NotBlank String razaoSocial,
        String nomeFantasia,
        @NotBlank String cnpj,
        @NotBlank String numeroLicenca,
        @NotNull LocalDate dataVencimentoLicenca,
        String nomeContato,
        String telefone,
        String email
) {}
