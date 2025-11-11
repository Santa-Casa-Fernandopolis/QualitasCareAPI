package com.erp.qualitascareapi.iam.repo;

import com.erp.qualitascareapi.iam.domain.Setor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SetorRepository extends JpaRepository<Setor, Long> {

    Page<Setor> findAllByTenantId(Long tenantId, Pageable pageable);

    Optional<Setor> findByTenantIdAndNomeIgnoreCase(Long tenantId, String nome);
}
