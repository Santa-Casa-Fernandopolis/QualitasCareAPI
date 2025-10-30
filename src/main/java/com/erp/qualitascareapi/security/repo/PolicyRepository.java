package com.erp.qualitascareapi.security.repo;

import com.erp.qualitascareapi.security.domains.Policy;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    @Query("""
      select distinct pol from Policy pol
       left join fetch pol.roles r
       left join fetch pol.conditions c
       where pol.tenant.id = :tenantId
         and pol.enabled = true
         and pol.resource = :res
         and pol.action = :act
         and (pol.feature = :feature or pol.feature is null)
       order by pol.priority asc
    """)
    List<Policy> findEffective(@Param("tenantId") Long tenantId,
                               @Param("res") ResourceType res,
                               @Param("act") Action act,
                               @Param("feature") String feature);
}

