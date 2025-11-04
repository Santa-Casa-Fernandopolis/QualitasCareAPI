package com.erp.qualitascareapi.iam.api.dto;

public record TenantDto(Long id,
                        Long code,
                        String name,
                        String cnpj,
                        String logo,
                        boolean active) {
}
