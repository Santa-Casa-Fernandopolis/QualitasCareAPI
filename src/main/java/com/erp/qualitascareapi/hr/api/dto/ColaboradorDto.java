package com.erp.qualitascareapi.hr.api.dto;

import com.erp.qualitascareapi.hr.enums.ColaboradorStatus;

import java.time.LocalDate;

public record ColaboradorDto(
        Long id,
        Long tenantId,
        String tenantNome,
        String matricula,
        String nomeCompleto,
        String cpf,
        String email,
        String telefone,
        LocalDate dataAdmissao,
        ColaboradorStatus status,
        Long setorId,
        String setorNome,
        Long cargoId,
        String cargoNome,
        Long usuarioSistemaId,
        String usuarioSistemaUsername
) {
}
