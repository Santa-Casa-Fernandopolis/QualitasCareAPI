package com.erp.qualitascareapi.hr.repo;

import com.erp.qualitascareapi.hr.domain.Cargo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CargoRepository extends JpaRepository<Cargo, Long> {

    Page<Cargo> findAllByTenant_Id(Long tenantId, Pageable pageable);

    Optional<Cargo> findByTenant_IdAndCodigoIgnoreCase(Long tenantId, String codigo);
}
