package com.erp.qualitascareapi.security.repo;

import com.erp.qualitascareapi.security.domains.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
