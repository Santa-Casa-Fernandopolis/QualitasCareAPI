package com.erp.qualitascareapi.hr.api.dto;

import com.erp.qualitascareapi.hr.enums.ColaboradorStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ColaboradorRequest(
        @NotNull(message = "Informe o tenant do colaborador") Long tenantId,
        @NotBlank(message = "Informe a matrícula do colaborador")
        @Size(max = 40, message = "A matrícula deve ter no máximo 40 caracteres")
        String matricula,
        @NotBlank(message = "Informe o nome completo do colaborador")
        @Size(max = 180, message = "O nome completo deve ter no máximo 180 caracteres")
        String nomeCompleto,
        @NotBlank(message = "Informe o CPF do colaborador")
        @Size(max = 14, message = "O CPF deve ter no máximo 14 caracteres")
        String cpf,
        @Email(message = "Informe um e-mail válido")
        @Size(max = 180, message = "O e-mail deve ter no máximo 180 caracteres")
        String email,
        @Size(max = 30, message = "O telefone deve ter no máximo 30 caracteres")
        String telefone,
        @NotNull(message = "Informe o setor do colaborador") Long setorId,
        @NotNull(message = "Informe o cargo do colaborador") Long cargoId,
        @PastOrPresent(message = "A data de admissão não pode estar no futuro")
        LocalDate dataAdmissao,
        @NotNull(message = "Informe o status do colaborador")
        ColaboradorStatus status,
        Long usuarioSistemaId
) {
}
