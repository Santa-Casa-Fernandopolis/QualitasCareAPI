package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.HigienizacaoAutoclaveProfunda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HigienizacaoAutoclaveProfundaRepository extends JpaRepository<HigienizacaoAutoclaveProfunda, Long> {
    Page<HigienizacaoAutoclaveProfunda> findAllByAutoclave_TenantId(Long tenantId, Pageable pageable);
}
