package com.erp.qualitascareapi.same.api.dto;

import com.erp.qualitascareapi.same.enums.SameSex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SamePatientRequest(
        @NotNull Long tenantId,
        @NotBlank @Size(max = 180) String fullName,
        @Size(max = 180) String motherName,
        LocalDate birthDate,
        @Size(max = 14) String cpf,
        @Size(max = 20) String cns,
        SameSex sex
) {
}
