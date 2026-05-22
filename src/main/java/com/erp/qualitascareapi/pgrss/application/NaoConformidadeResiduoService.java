package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.pgrss.api.dto.NaoConformidadeResiduoDto;
import com.erp.qualitascareapi.pgrss.api.dto.NaoConformidadeResiduoRequest;
import com.erp.qualitascareapi.pgrss.domain.GrupoResiduo;
import com.erp.qualitascareapi.pgrss.domain.NaoConformidadeResiduo;
import com.erp.qualitascareapi.pgrss.domain.SetorGerador;
import com.erp.qualitascareapi.pgrss.domain.TipoResiduo;
import com.erp.qualitascareapi.pgrss.enums.SeveridadeNaoConformidade;
import com.erp.qualitascareapi.pgrss.enums.StatusNaoConformidade;
import com.erp.qualitascareapi.pgrss.repo.GrupoResiduoRepository;
import com.erp.qualitascareapi.pgrss.repo.NaoConformidadeResiduoRepository;
import com.erp.qualitascareapi.pgrss.repo.SetorGeradorRepository;
import com.erp.qualitascareapi.pgrss.repo.TipoResiduoRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NaoConformidadeResiduoService {

    private final NaoConformidadeResiduoRepository repository;
    private final SetorGeradorRepository setorRepository;
    private final GrupoResiduoRepository grupoRepository;
    private final TipoResiduoRepository tipoRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public NaoConformidadeResiduoService(NaoConformidadeResiduoRepository repository,
                                          SetorGeradorRepository setorRepository,
                                          GrupoResiduoRepository grupoRepository,
                                          TipoResiduoRepository tipoRepository,
                                          TenantRepository tenantRepository,
                                          TenantScopeGuard tenantScopeGuard) {
        this.repository = repository;
        this.setorRepository = setorRepository;
        this.grupoRepository = grupoRepository;
        this.tipoRepository = tipoRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public NaoConformidadeResiduoDto registrar(NaoConformidadeResiduoRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        Tenant tenant = tenantRepository.findById(req.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        NaoConformidadeResiduo e = new NaoConformidadeResiduo();
        e.setTenant(tenant);
        e.setDataHoraOcorrencia(req.dataHoraOcorrencia());
        e.setTipoNaoConformidade(req.tipoNaoConformidade());
        e.setSeveridade(req.severidade());
        e.setDescricao(req.descricao());
        e.setAcaoImediata(req.acaoImediata());
        e.setAreaResponsavel(req.areaResponsavel());
        e.setCriadoPorNome(req.criadoPorNome());
        e.setStatus(StatusNaoConformidade.ABERTA);
        if (req.setorId() != null) {
            SetorGerador setor = setorRepository.findById(req.setorId())
                    .orElseThrow(() -> new EntityNotFoundException("Setor não encontrado: " + req.setorId()));
            e.setSetor(setor);
        }
        if (req.grupoId() != null) {
            GrupoResiduo grupo = grupoRepository.findById(req.grupoId())
                    .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado: " + req.grupoId()));
            e.setGrupo(grupo);
        }
        if (req.tipoId() != null) {
            TipoResiduo tipo = tipoRepository.findById(req.tipoId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo não encontrado: " + req.tipoId()));
            e.setTipo(tipo);
        }
        // Auto-set exige plano de ação para severidade CRITICA
        if (SeveridadeNaoConformidade.CRITICA.equals(req.severidade())) {
            e.setExigePlanoAcao(true);
        }
        return toDto(repository.save(e));
    }

    public NaoConformidadeResiduoDto atualizarStatus(Long id, StatusNaoConformidade novoStatus) {
        NaoConformidadeResiduo e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setStatus(novoStatus);
        return toDto(repository.save(e));
    }

    @Transactional(readOnly = true)
    public Page<NaoConformidadeResiduoDto> search(Long setorId, SeveridadeNaoConformidade severidade,
                                                   StatusNaoConformidade status, Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return repository.findAllByTenant_Id(tenantId, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public NaoConformidadeResiduoDto findById(Long id) {
        NaoConformidadeResiduo e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        return toDto(e);
    }

    private NaoConformidadeResiduo findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não conformidade não encontrada: " + id));
    }

    private NaoConformidadeResiduoDto toDto(NaoConformidadeResiduo e) {
        return new NaoConformidadeResiduoDto(
                e.getId(),
                e.getTenant().getId(),
                e.getSetor() != null ? e.getSetor().getId() : null,
                e.getSetor() != null ? e.getSetor().getNome() : null,
                e.getDataHoraOcorrencia(),
                e.getTipoNaoConformidade(),
                e.getSeveridade(),
                e.getDescricao(),
                e.getAcaoImediata(),
                e.getAreaResponsavel(),
                e.isExigePlanoAcao(),
                e.getStatus(),
                e.getCriadoPorNome()
        );
    }
}
