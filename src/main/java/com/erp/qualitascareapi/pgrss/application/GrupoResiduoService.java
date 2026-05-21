package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.pgrss.api.dto.GrupoResiduoDto;
import com.erp.qualitascareapi.pgrss.api.dto.GrupoResiduoRequest;
import com.erp.qualitascareapi.pgrss.domain.GrupoResiduo;
import com.erp.qualitascareapi.pgrss.repo.GrupoResiduoRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GrupoResiduoService {

    private final GrupoResiduoRepository repository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public GrupoResiduoService(GrupoResiduoRepository repository,
                               TenantRepository tenantRepository,
                               TenantScopeGuard tenantScopeGuard) {
        this.repository = repository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public GrupoResiduoDto create(GrupoResiduoRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        Tenant tenant = loadTenant(req.tenantId());
        GrupoResiduo entity = new GrupoResiduo();
        applyRequest(entity, req, tenant);
        return toDto(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<GrupoResiduoDto> list(Pageable pageable) {
        return repository.findAllByTenant_Id(tenantScopeGuard.currentTenantId(), pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<GrupoResiduoDto> listAtivos() {
        return repository.findAllByTenant_IdAndAtivo(tenantScopeGuard.currentTenantId(), Boolean.TRUE)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public GrupoResiduoDto findById(Long id) {
        return toDto(loadEntity(id));
    }

    public GrupoResiduoDto update(Long id, GrupoResiduoRequest req) {
        GrupoResiduo entity = loadEntity(id);
        tenantScopeGuard.checkRequestedTenant(entity.getTenant().getId());
        Tenant tenant = loadTenant(req.tenantId());
        applyRequest(entity, req, tenant);
        return toDto(repository.save(entity));
    }

    public GrupoResiduoDto toggleAtivo(Long id, Boolean ativo) {
        GrupoResiduo entity = loadEntity(id);
        tenantScopeGuard.checkRequestedTenant(entity.getTenant().getId());
        entity.setAtivo(ativo);
        return toDto(repository.save(entity));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void applyRequest(GrupoResiduo entity, GrupoResiduoRequest req, Tenant tenant) {
        entity.setTenant(tenant);
        entity.setCodigo(req.codigo());
        entity.setNome(req.nome());
        entity.setDescricao(req.descricao());
        entity.setCorIdentificacao(req.corIdentificacao());
    }

    private GrupoResiduo loadEntity(Long id) {
        GrupoResiduo entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GrupoResiduo não encontrado: " + id));
        tenantScopeGuard.checkRequestedTenant(entity.getTenant().getId());
        return entity;
    }

    private Tenant loadTenant(Long tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado: " + tenantId));
    }

    GrupoResiduoDto toDto(GrupoResiduo e) {
        return new GrupoResiduoDto(
                e.getId(),
                e.getTenant().getId(),
                e.getCodigo(),
                e.getNome(),
                e.getDescricao(),
                e.getCorIdentificacao(),
                e.getAtivo()
        );
    }
}
