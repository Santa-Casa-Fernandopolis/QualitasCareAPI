package com.erp.qualitascareapi.iam.repo;

import com.erp.qualitascareapi.iam.domain.SetorEspecialidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SetorEspecialidadeRepository extends JpaRepository<SetorEspecialidade, Long> {

    Page<SetorEspecialidade> findAllByTenantId(Long tenantId, Pageable pageable);

    List<SetorEspecialidade> findAllByTenantIdAndActiveTrueOrderByNomeAsc(Long tenantId);

    Optional<SetorEspecialidade> findByTenantIdAndNomeIgnoreCase(Long tenantId, String nome);
}

