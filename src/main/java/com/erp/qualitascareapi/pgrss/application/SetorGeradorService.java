package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.SetorRepository;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.pgrss.api.dto.SetorGeradorDto;
import com.erp.qualitascareapi.pgrss.api.dto.SetorGeradorRequest;
import com.erp.qualitascareapi.pgrss.domain.SetorGerador;
import com.erp.qualitascareapi.pgrss.repo.SetorGeradorRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SetorGeradorService {

    private final SetorGeradorRepository repository;
    private final TenantRepository tenantRepository;
    private final SetorRepository setorRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public SetorGeradorService(SetorGeradorRepository repository,
                               TenantRepository tenantRepository,
                               SetorRepository setorRepository,
                               TenantScopeGuard tenantScopeGuard) {
        this.repository = repository;
        this.tenantRepository = tenantRepository;
        this.setorRepository = setorRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public SetorGeradorDto create(SetorGeradorRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        Tenant tenant = loadTenant(req.tenantId());
        SetorGerador entity = new SetorGerador();
        applyRequest(entity, req, tenant);
        return toDto(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<SetorGeradorDto> list(Boolean ativo, Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (ativo != null) {
            return repository.findAllByTenant_IdAndAtivo(tenantId, ativo, pageable).map(this::toDto);
        }
        return repository.findAllByTenant_Id(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public SetorGeradorDto findById(Long id) {
        return toDto(loadEntity(id));
    }

    public SetorGeradorDto update(Long id, SetorGeradorRequest req) {
        SetorGerador entity = loadEntity(id);
        tenantScopeGuard.checkRequestedTenant(entity.getTenant().getId());
        Tenant tenant = loadTenant(req.tenantId());
        applyRequest(entity, req, tenant);
        return toDto(repository.save(entity));
    }

    public SetorGeradorDto toggleAtivo(Long id, Boolean ativo) {
        SetorGerador entity = loadEntity(id);
        tenantScopeGuard.checkRequestedTenant(entity.getTenant().getId());
        entity.setAtivo(ativo);
        return toDto(repository.save(entity));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void applyRequest(SetorGerador entity, SetorGeradorRequest req, Tenant tenant) {
        entity.setTenant(tenant);
        entity.setNome(req.nome());
        entity.setCodigoInterno(req.codigoInterno());
        entity.setTipo(req.tipo());
        entity.setDescricao(req.descricao());
        if (req.setorId() != null) {
            Setor setor = setorRepository.findById(req.setorId())
                    .orElseThrow(() -> new EntityNotFoundException("Setor não encontrado: " + req.setorId()));
            entity.setSetor(setor);
        } else {
            entity.setSetor(null);
        }
    }

    private SetorGerador loadEntity(Long id) {
        SetorGerador entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SetorGerador não encontrado: " + id));
        tenantScopeGuard.checkRequestedTenant(entity.getTenant().getId());
        return entity;
    }

    private Tenant loadTenant(Long tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado: " + tenantId));
    }

    private SetorGeradorDto toDto(SetorGerador e) {
        return new SetorGeradorDto(
                e.getId(),
                e.getTenant().getId(),
                e.getSetor() != null ? e.getSetor().getId() : null,
                e.getNome(),
                e.getCodigoInterno(),
                e.getTipo(),
                e.getDescricao(),
                e.getAtivo()
        );
    }
}
