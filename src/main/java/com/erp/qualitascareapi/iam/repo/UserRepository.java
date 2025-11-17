package com.erp.qualitascareapi.iam.repo;

import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.projection.UserAuthorizationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"roles", "tenant"})
    Optional<User> findByUsernameIgnoreCase(String username);

    @EntityGraph(attributePaths = {"roles", "tenant"})
    Optional<User> findByUsernameIgnoreCaseAndTenant_Code(String username, Long tenantCode);

    @EntityGraph(attributePaths = {"tenant"})
    List<User> findAllByUsernameIgnoreCase(String username);

    @EntityGraph(attributePaths = {"roles", "tenant"})
    Page<User> findAllByTenant_Id(Long tenantId, Pageable pageable);

    @Query("""
            select u.id as id,
                   u.tenant.id as tenantId,
                   u.department as department,
                   u.status as status,
                   u.origin as origin,
                   u.username as username
            from User u
            where u.id = :id and u.tenant.id = :tenantId
            """)
    Optional<UserAuthorizationProjection> findAuthorizationProjectionByIdAndTenantId(@Param("id") Long id,
                                                                                    @Param("tenantId") Long tenantId);
}
