package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.SecagemMaterial;
import com.erp.qualitascareapi.cme.enums.TipoSecagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecagemMaterialRepository extends JpaRepository<SecagemMaterial, Long> {
    List<SecagemMaterial> findByProcessoId(Long processoId);
    List<SecagemMaterial> findByTenantIdAndTipoSecagem(Long tenantId, TipoSecagem tipoSecagem);
    Page<SecagemMaterial> findAllByTenantId(Long tenantId, Pageable pageable);
}
