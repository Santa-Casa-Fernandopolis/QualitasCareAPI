package com.erp.qualitascareapi.iam.repo;

import com.erp.qualitascareapi.iam.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"roles", "tenant"})
    Optional<User> findByUsernameIgnoreCase(String username);

    @EntityGraph(attributePaths = {"roles", "tenant"})
    Optional<User> findByUsernameIgnoreCaseAndTenant_CodeIgnoreCase(String username, String tenantCode);
}
