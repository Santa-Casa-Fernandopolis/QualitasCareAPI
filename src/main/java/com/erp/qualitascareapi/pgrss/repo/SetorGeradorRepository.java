package com.erp.qualitascareapi.pgrss.repo;

import com.erp.qualitascareapi.pgrss.domain.SetorGerador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetorGeradorRepository extends JpaRepository<SetorGerador, Long> {

    Page<SetorGerador> findAllByTenant_Id(Long tenantId, Pageable pageable);

    List<SetorGerador> findAllByTenant_IdAndAtivoTrue(Long tenantId);
}
