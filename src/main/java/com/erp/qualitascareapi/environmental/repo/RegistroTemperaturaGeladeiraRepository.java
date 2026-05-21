package com.erp.qualitascareapi.environmental.repo;

import com.erp.qualitascareapi.environmental.domain.RegistroTemperaturaGeladeira;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroTemperaturaGeladeiraRepository extends JpaRepository<RegistroTemperaturaGeladeira, Long> {
    Page<RegistroTemperaturaGeladeira> findAllByTenantId(Long tenantId, Pageable pageable);
    Page<RegistroTemperaturaGeladeira> findAllByTenantIdAndGeladeiraId(Long tenantId, Long geladeiraId, Pageable pageable);
}
