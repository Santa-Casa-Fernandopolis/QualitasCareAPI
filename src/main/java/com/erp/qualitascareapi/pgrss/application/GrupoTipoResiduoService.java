package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.pgrss.api.dto.*;
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
public class GrupoTipoResiduoService {

    private final GrupoResiduoRepository grupoRepository;
    private final TipoResiduoRepository tipoRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public GrupoTipoResiduoService(GrupoResiduoRepository grupoRepository,
                                    TipoResiduoRepository tipoRepository,
                                    TenantRepository tenantRepository,
                                    TenantScopeGuard tenantScopeGuard) {
        this.grupoRepository = grupoRepository;
        this.tipoRepository = tipoRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public GrupoResiduoDto createGrupo(GrupoResiduoRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        if (grupoRepository.findByTenant_IdAndCodigo(req.tenantId(), req.codigo()).isPresent()) {
            throw new IllegalStateException("Já existe um grupo com o código '" + req.codigo() + "' para este tenant");
        }
        Tenant tenant = tenantRepository.findById(req.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        GrupoResiduo e = new GrupoResiduo();
        e.setTenant(tenant);
        e.setCodigo(req.codigo());
        e.setNome(req.nome());
        e.setDescricao(req.descricao());
        e.setRisco(req.risco());
        e.setPadraoCorIdentificacao(req.padraoCorIdentificacao());
        e.setRequerTratamento(req.requerTratamento() != null && req.requerTratamento());
        e.setAtivo(true);
        return toGrupoDto(grupoRepository.save(e));
    }

    @Transactional(readOnly = true)
    public Page<GrupoResiduoDto> listGrupos(Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return grupoRepository.findAllByTenant_Id(tenantId, pageable).map(this::toGrupoDto);
    }

    @Transactional(readOnly = true)
    public List<GrupoResiduoDto> listGruposAtivos() {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return grupoRepository.findAllByTenant_IdAndAtivoTrue(tenantId).stream()
                .map(this::toGrupoDto).toList();
    }

    @Transactional(readOnly = true)
    public GrupoResiduoDto findGrupoById(Long id) {
        GrupoResiduo e = grupoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grupo de resíduo não encontrado: " + id));
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        return toGrupoDto(e);
    }

    public GrupoResiduoDto updateGrupo(Long id, GrupoResiduoRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        GrupoResiduo e = grupoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grupo de resíduo não encontrado: " + id));
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setNome(req.nome());
        e.setDescricao(req.descricao());
        e.setRisco(req.risco());
        e.setPadraoCorIdentificacao(req.padraoCorIdentificacao());
        e.setRequerTratamento(req.requerTratamento() != null && req.requerTratamento());
        return toGrupoDto(grupoRepository.save(e));
    }

    public GrupoResiduoDto toggleGrupoAtivo(Long id) {
        GrupoResiduo e = grupoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grupo de resíduo não encontrado: " + id));
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setAtivo(!e.isAtivo());
        return toGrupoDto(grupoRepository.save(e));
    }

    public TipoResiduoDto createTipo(TipoResiduoRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        Tenant tenant = tenantRepository.findById(req.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        GrupoResiduo grupo = grupoRepository.findById(req.grupoId())
                .orElseThrow(() -> new EntityNotFoundException("Grupo de resíduo não encontrado: " + req.grupoId()));
        if (!grupo.getTenant().getId().equals(req.tenantId())) {
            throw new IllegalStateException("Grupo não pertence ao tenant informado");
        }
        TipoResiduo e = new TipoResiduo();
        e.setTenant(tenant);
        e.setGrupo(grupo);
        e.setNome(req.nome());
        e.setDescricao(req.descricao());
        e.setTipoAcondicionamento(req.tipoAcondicionamento());
        e.setRequerIdentificacao(req.requerIdentificacao() != null && req.requerIdentificacao());
        e.setRequerPesagem(req.requerPesagem() == null || req.requerPesagem());
        e.setAtivo(true);
        return toTipoDto(tipoRepository.save(e));
    }

    public TipoResiduoDto updateTipo(Long id, TipoResiduoRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        TipoResiduo e = findTipoEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        GrupoResiduo grupo = grupoRepository.findById(req.grupoId())
                .orElseThrow(() -> new EntityNotFoundException("Grupo de resíduo não encontrado: " + req.grupoId()));
        if (!grupo.getTenant().getId().equals(req.tenantId())) {
            throw new IllegalStateException("Grupo não pertence ao tenant informado");
        }
        e.setGrupo(grupo);
        e.setNome(req.nome());
        e.setDescricao(req.descricao());
        e.setTipoAcondicionamento(req.tipoAcondicionamento());
        e.setRequerIdentificacao(req.requerIdentificacao() != null && req.requerIdentificacao());
        e.setRequerPesagem(req.requerPesagem() == null || req.requerPesagem());
        return toTipoDto(tipoRepository.save(e));
    }

    public TipoResiduoDto toggleTipoAtivo(Long id) {
        TipoResiduo e = findTipoEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setAtivo(!e.isAtivo());
        return toTipoDto(tipoRepository.save(e));
    }

    @Transactional(readOnly = true)
    public Page<TipoResiduoDto> listTipos(Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return tipoRepository.findAllByTenant_Id(tenantId, pageable).map(this::toTipoDto);
    }

    @Transactional(readOnly = true)
    public List<TipoResiduoDto> listTiposByGrupo(Long grupoId) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return tipoRepository.findAllByTenant_IdAndGrupo_IdAndAtivoTrue(tenantId, grupoId).stream()
                .map(this::toTipoDto).toList();
    }

    @Transactional(readOnly = true)
    public TipoResiduoDto findTipoById(Long id) {
        TipoResiduo e = findTipoEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        return toTipoDto(e);
    }

    private TipoResiduo findTipoEntity(Long id) {
        return tipoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de resíduo não encontrado: " + id));
    }

    private GrupoResiduoDto toGrupoDto(GrupoResiduo e) {
        return new GrupoResiduoDto(
                e.getId(),
                e.getTenant().getId(),
                e.getCodigo(),
                e.getNome(),
                e.getDescricao(),
                e.getRisco(),
                e.getPadraoCorIdentificacao(),
                e.isRequerTratamento(),
                e.isAtivo()
        );
    }

    private TipoResiduoDto toTipoDto(TipoResiduo e) {
        return new TipoResiduoDto(
                e.getId(),
                e.getTenant().getId(),
                e.getGrupo().getId(),
                e.getGrupo().getNome(),
                e.getNome(),
                e.getDescricao(),
                e.getTipoAcondicionamento(),
                e.isRequerIdentificacao(),
                e.isRequerPesagem(),
                e.isAtivo()
        );
    }
}
