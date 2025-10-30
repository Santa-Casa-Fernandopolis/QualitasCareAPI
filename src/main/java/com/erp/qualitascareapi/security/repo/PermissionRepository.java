package com.erp.qualitascareapi.security.repo;

import com.erp.qualitascareapi.security.domains.Permission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query("""
      select p from Permission p
       where p.tenant.id = :tenantId
         and p.resource = :res
         and p.action = :act
         and (p.feature = :feature or p.feature is null)
    """)
    List<Permission> findScope(@Param("tenantId") Long tenantId,
                               @Param("res") ResourceType res,
                               @Param("act") Action act,
                               @Param("feature") String feature);
}

