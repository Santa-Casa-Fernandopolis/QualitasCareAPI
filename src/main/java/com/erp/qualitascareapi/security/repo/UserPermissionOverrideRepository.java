package com.erp.qualitascareapi.security.repo;

import com.erp.qualitascareapi.security.domains.UserPermissionOverride;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserPermissionOverrideRepository extends JpaRepository<UserPermissionOverride, Long> {

    @Query("""
      select o from UserPermissionOverride o
       where o.user.id   = :userId
         and o.tenant.id = :tenantId
         and o.resource  = :res
         and o.action    = :act
         and (o.feature  = :feature or o.feature is null)
         and o.approved = true
         and (o.validFrom is null or o.validFrom <= :now)
         and (o.validUntil is null or o.validUntil >= :now)
       order by o.priority asc
    """)
    Optional<UserPermissionOverride> findEffective(@Param("userId") Long userId,
                                                   @Param("tenantId") Long tenantId,
                                                   @Param("res") ResourceType res,
                                                   @Param("act") Action act,
                                                   @Param("feature") String feature,
                                                   @Param("now") LocalDateTime now);
}

