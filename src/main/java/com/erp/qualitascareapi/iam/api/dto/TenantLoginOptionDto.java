package com.erp.qualitascareapi.iam.api.dto;

public record TenantLoginOptionDto(Long id,
                                   String code,
                                   String name,
                                   String cnpj,
                                   String logo) {
}
