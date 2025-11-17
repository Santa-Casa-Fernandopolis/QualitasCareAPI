package com.erp.qualitascareapi.security.repo;

import com.erp.qualitascareapi.security.domain.Permission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Page<Permission> findAllByTenant_Id(Long tenantId, Pageable pageable);

    @Query("""
        select p from Permission p
        where p.tenant.id = :tenantId
          and (:resource is null or p.resource = :resource)
          and (:action is null or p.action = :action)
          and (:feature is null or lower(p.feature) like lower(concat('%', :feature, '%')))
          and (:code is null or lower(p.code) like lower(concat('%', :code, '%')))
    """)
    Page<Permission> search(@Param("tenantId") Long tenantId,
                            @Param("resource") ResourceType resource,
                            @Param("action") Action action,
                            @Param("feature") String feature,
                            @Param("code") String code,
                            Pageable pageable);

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

    Optional<Permission> findByTenant_IdAndResourceAndActionAndFeature(Long tenantId,
                                                                       ResourceType resource,
                                                                       Action action,
                                                                       String feature);
}

