package com.erp.qualitascareapi.iam.api.dto;

public record TenantLoginOptionDto(Long id,
                                   Long code,
                                   String name,
                                   String cnpj,
                                   String logo) {
}
