package com.erp.qualitascareapi.same.api.dto;

import com.erp.qualitascareapi.same.enums.SamePatientStatus;
import jakarta.validation.constraints.NotNull;

public record SamePatientStatusRequest(@NotNull SamePatientStatus status) {
}
