package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.EmpresaColetoraTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EmpresaColetorRequest(
        @NotNull Long tenantId,
        @NotBlank String razaoSocial,
        String cnpj,
        @NotNull EmpresaColetoraTipo tipo,
        String licencaNumero,
        LocalDate licencaVencimento,
        String telefone,
        String email,
        String responsavelNome,
        String observacoes
) {}
