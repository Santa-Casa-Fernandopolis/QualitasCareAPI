package com.erp.qualitascareapi.security.repo;

import com.erp.qualitascareapi.security.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Page<Role> findAllByTenant_Id(Long tenantId, Pageable pageable);

    Optional<Role> findByNameIgnoreCaseAndTenant_Id(String name, Long tenantId);
}
