package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
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

import java.util.List;

@Service
@Transactional
public class SetorGeradorService {

    private final SetorGeradorRepository repository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public SetorGeradorService(SetorGeradorRepository repository,
                               TenantRepository tenantRepository,
                               TenantScopeGuard tenantScopeGuard) {
        this.repository = repository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public SetorGeradorDto create(SetorGeradorRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        Tenant tenant = tenantRepository.findById(req.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        SetorGerador e = new SetorGerador();
        e.setTenant(tenant);
        e.setNome(req.nome());
        e.setCentroCusto(req.centroCusto());
        e.setTipo(req.tipo());
        e.setObservacoes(req.observacoes());
        e.setAtivo(true);
        return toDto(repository.save(e));
    }

    public SetorGeradorDto update(Long id, SetorGeradorRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        SetorGerador e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setNome(req.nome());
        e.setCentroCusto(req.centroCusto());
        e.setTipo(req.tipo());
        e.setObservacoes(req.observacoes());
        return toDto(repository.save(e));
    }

    public SetorGeradorDto toggleAtivo(Long id) {
        SetorGerador e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setAtivo(!e.isAtivo());
        return toDto(repository.save(e));
    }

    @Transactional(readOnly = true)
    public Page<SetorGeradorDto> findAll(Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return repository.findAllByTenant_Id(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<SetorGeradorDto> findAllAtivos() {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return repository.findAllByTenant_IdAndAtivoTrue(tenantId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public SetorGeradorDto findById(Long id) {
        SetorGerador e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        return toDto(e);
    }

    private SetorGerador findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Setor gerador não encontrado: " + id));
    }

    private SetorGeradorDto toDto(SetorGerador e) {
        return new SetorGeradorDto(
                e.getId(),
                e.getTenant().getId(),
                e.getNome(),
                e.getCentroCusto(),
                e.getTipo(),
                e.isAtivo(),
                e.getObservacoes()
        );
    }
}
