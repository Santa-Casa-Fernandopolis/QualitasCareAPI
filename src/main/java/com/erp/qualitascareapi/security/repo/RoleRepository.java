package com.erp.qualitascareapi.security.repo;

import com.erp.qualitascareapi.security.domains.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByNameIgnoreCaseAndTenant_Id(String name, Long tenantId);
}
