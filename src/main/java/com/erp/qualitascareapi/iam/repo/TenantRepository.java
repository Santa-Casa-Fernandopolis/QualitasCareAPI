package com.erp.qualitascareapi.iam.repo;

import com.erp.qualitascareapi.iam.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByCode(Long code);
}
