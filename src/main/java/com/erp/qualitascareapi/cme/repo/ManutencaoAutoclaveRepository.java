package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.ManutencaoAutoclave;
import com.erp.qualitascareapi.cme.enums.ManutencaoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManutencaoAutoclaveRepository extends JpaRepository<ManutencaoAutoclave, Long> {
    Page<ManutencaoAutoclave> findAllByAutoclave_TenantId(Long tenantId, Pageable pageable);

    long countByAutoclave_TenantIdAndStatusIn(Long tenantId, List<ManutencaoStatus> statuses);
}
