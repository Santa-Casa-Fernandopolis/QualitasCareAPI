package com.erp.qualitascareapi.security.repo;

import com.erp.qualitascareapi.security.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Page<Role> findAllByTenant_Id(Long tenantId, Pageable pageable);

    @Query("""
        select r from Role r
        where r.tenant.id = :tenantId
          and (:name is null or lower(r.name) like lower(concat('%', :name, '%')))
          and (:description is null or lower(r.description) like lower(concat('%', :description, '%')))
    """)
    Page<Role> search(@Param("tenantId") Long tenantId,
                      @Param("name") String name,
                      @Param("description") String description,
                      Pageable pageable);

    Optional<Role> findByNameIgnoreCaseAndTenant_Id(String name, Long tenantId);
}
