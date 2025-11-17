package com.erp.qualitascareapi.security.repo;

import com.erp.qualitascareapi.security.domain.Policy;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import com.erp.qualitascareapi.security.enums.Effect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Page<Policy> findAllByTenant_Id(Long tenantId, Pageable pageable);

    @Query("""
        select pol from Policy pol
        where pol.tenant.id = :tenantId
          and (:resource is null or pol.resource = :resource)
          and (:action is null or pol.action = :action)
          and (:feature is null or lower(pol.feature) like lower(concat('%', :feature, '%')))
          and (:effect is null or pol.effect = :effect)
          and (:enabled is null or pol.enabled = :enabled)
          and (:description is null or lower(pol.description) like lower(concat('%', :description, '%')))
    """)
    Page<Policy> search(@Param("tenantId") Long tenantId,
                        @Param("resource") ResourceType resource,
                        @Param("action") Action action,
                        @Param("feature") String feature,
                        @Param("effect") Effect effect,
                        @Param("enabled") Boolean enabled,
                        @Param("description") String description,
                        Pageable pageable);

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

