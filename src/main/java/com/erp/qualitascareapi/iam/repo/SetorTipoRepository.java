package com.erp.qualitascareapi.iam.repo;

import com.erp.qualitascareapi.iam.domain.SetorTipoCadastro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SetorTipoRepository extends JpaRepository<SetorTipoCadastro, Long> {

    Page<SetorTipoCadastro> findAllByTenantId(Long tenantId, Pageable pageable);

    List<SetorTipoCadastro> findAllByTenantIdAndActiveTrueOrderByNomeAsc(Long tenantId);

    Optional<SetorTipoCadastro> findByTenantIdAndNomeIgnoreCase(Long tenantId, String nome);
}

