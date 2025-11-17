package com.erp.qualitascareapi.security.repo;

import com.erp.qualitascareapi.security.domain.Role;
import com.erp.qualitascareapi.security.domain.RolePermission;
import com.erp.qualitascareapi.security.domain.Permission;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    @Query("""
      select case when count(rp) > 0 then true else false end
        from RolePermission rp
        join rp.role r
        join rp.permission p
       where rp.tenant.id = :tenantId
         and upper(r.name) in :roleNames
         and p.resource = :res
         and p.action = :act
         and (p.feature = :feature or p.feature is null)
    """)
    boolean existsByRolesAndScope(@Param("roleNames") Set<String> roleNames,
                                  @Param("tenantId") Long tenantId,
                                  @Param("res") ResourceType res,
                                  @Param("act") Action act,
                                  @Param("feature") String feature);

    @Query("""
      select distinct p.code
        from RolePermission rp
        join rp.role r
        join rp.permission p
       where rp.tenant.id = :tenantId
         and upper(r.name) in :roleNames
         and p.code is not null
    """)
    Set<String> findPermissionCodesByRolesAndTenant(@Param("roleNames") Set<String> roleNames,
                                                    @Param("tenantId") Long tenantId);

    boolean existsByRoleAndPermissionAndTenant(Role role, Permission permission, Tenant tenant);
}

