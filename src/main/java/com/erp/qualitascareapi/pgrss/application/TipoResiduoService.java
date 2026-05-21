package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.pgrss.api.dto.TipoResiduoDto;
import com.erp.qualitascareapi.pgrss.api.dto.TipoResiduoRequest;
import com.erp.qualitascareapi.pgrss.domain.GrupoResiduo;
import com.erp.qualitascareapi.pgrss.domain.TipoResiduo;
import com.erp.qualitascareapi.pgrss.repo.GrupoResiduoRepository;
import com.erp.qualitascareapi.pgrss.repo.TipoResiduoRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TipoResiduoService {

    private final TipoResiduoRepository repository;
    private final GrupoResiduoRepository grupoRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public TipoResiduoService(TipoResiduoRepository repository,
                              GrupoResiduoRepository grupoRepository,
                              TenantRepository tenantRepository,
                              TenantScopeGuard tenantScopeGuard) {
        this.repository = repository;
        this.grupoRepository = grupoRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public TipoResiduoDto create(TipoResiduoRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        Tenant tenant = loadTenant(req.tenantId());
        GrupoResiduo grupo = loadGrupo(req.grupoResiduoId(), req.tenantId());
        TipoResiduo entity = new TipoResiduo();
        applyRequest(entity, req, tenant, grupo);
        return toDto(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<TipoResiduoDto> list(Boolean ativo, Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (ativo != null) {
            return repository.findAllByTenant_IdAndAtivo(tenantId, ativo, pageable).map(this::toDto);
        }
        return repository.findAllByTenant_Id(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<TipoResiduoDto> listByGrupo(Long grupoId) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return repository.findAllByTenant_IdAndGrupoResiduo_Id(tenantId, grupoId)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public TipoResiduoDto findById(Long id) {
        return toDto(loadEntity(id));
    }

    public TipoResiduoDto update(Long id, TipoResiduoRequest req) {
        TipoResiduo entity = loadEntity(id);
        tenantScopeGuard.checkRequestedTenant(entity.getTenant().getId());
        Tenant tenant = loadTenant(req.tenantId());
        GrupoResiduo grupo = loadGrupo(req.grupoResiduoId(), req.tenantId());
        applyRequest(entity, req, tenant, grupo);
        return toDto(repository.save(entity));
    }

    public TipoResiduoDto toggleAtivo(Long id, Boolean ativo) {
        TipoResiduo entity = loadEntity(id);
        tenantScopeGuard.checkRequestedTenant(entity.getTenant().getId());
        entity.setAtivo(ativo);
        return toDto(repository.save(entity));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void applyRequest(TipoResiduo entity, TipoResiduoRequest req, Tenant tenant, GrupoResiduo grupo) {
        entity.setTenant(tenant);
        entity.setGrupoResiduo(grupo);
        entity.setCodigo(req.codigo());
        entity.setNome(req.nome());
        entity.setDescricao(req.descricao());
        entity.setPericulosidade(req.periculosidade());
        entity.setEstadoFisico(req.estadoFisico());
        entity.setTipoAcondicionamento(req.tipoAcondicionamento());
        entity.setTipoTratamento(req.tipoTratamento());
        entity.setTipoDestinacaoFinal(req.tipoDestinacaoFinal());
        entity.setRequerLicenca(req.requerLicenca() != null ? req.requerLicenca() : Boolean.FALSE);
    }

    private TipoResiduo loadEntity(Long id) {
        TipoResiduo entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TipoResiduo não encontrado: " + id));
        tenantScopeGuard.checkRequestedTenant(entity.getTenant().getId());
        return entity;
    }

    private GrupoResiduo loadGrupo(Long grupoId, Long tenantId) {
        return grupoRepository.findById(grupoId)
                .filter(g -> g.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("GrupoResiduo não encontrado: " + grupoId));
    }

    private Tenant loadTenant(Long tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado: " + tenantId));
    }

    TipoResiduoDto toDto(TipoResiduo e) {
        return new TipoResiduoDto(
                e.getId(),
                e.getTenant().getId(),
                e.getGrupoResiduo().getId(),
                e.getGrupoResiduo().getNome(),
                e.getCodigo(),
                e.getNome(),
                e.getDescricao(),
                e.getPericulosidade(),
                e.getEstadoFisico(),
                e.getTipoAcondicionamento(),
                e.getTipoTratamento(),
                e.getTipoDestinacaoFinal(),
                e.getRequerLicenca(),
                e.getAtivo()
        );
    }
}
