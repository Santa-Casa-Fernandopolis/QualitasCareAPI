package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.pgrss.api.dto.PesagemFiltroRequest;
import com.erp.qualitascareapi.pgrss.api.dto.PesagemResiduoDto;
import com.erp.qualitascareapi.pgrss.api.dto.PesagemResiduoRequest;
import com.erp.qualitascareapi.pgrss.domain.GrupoResiduo;
import com.erp.qualitascareapi.pgrss.domain.PesagemResiduo;
import com.erp.qualitascareapi.pgrss.domain.SetorGerador;
import com.erp.qualitascareapi.pgrss.domain.TipoResiduo;
import com.erp.qualitascareapi.pgrss.enums.StatusPesagem;
import com.erp.qualitascareapi.pgrss.repo.GrupoResiduoRepository;
import com.erp.qualitascareapi.pgrss.repo.PesagemResiduoRepository;
import com.erp.qualitascareapi.pgrss.repo.SetorGeradorRepository;
import com.erp.qualitascareapi.pgrss.repo.TipoResiduoRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Transactional
public class PesagemResiduoService {

    private final PesagemResiduoRepository repository;
    private final SetorGeradorRepository setorRepository;
    private final TipoResiduoRepository tipoRepository;
    private final GrupoResiduoRepository grupoRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public PesagemResiduoService(PesagemResiduoRepository repository,
                                  SetorGeradorRepository setorRepository,
                                  TipoResiduoRepository tipoRepository,
                                  GrupoResiduoRepository grupoRepository,
                                  TenantRepository tenantRepository,
                                  TenantScopeGuard tenantScopeGuard) {
        this.repository = repository;
        this.setorRepository = setorRepository;
        this.tipoRepository = tipoRepository;
        this.grupoRepository = grupoRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public PesagemResiduoDto registrar(PesagemResiduoRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        if (req.pesoKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Peso deve ser maior que zero");
        }
        Tenant tenant = tenantRepository.findById(req.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        SetorGerador setor = setorRepository.findById(req.setorId())
                .orElseThrow(() -> new EntityNotFoundException("Setor gerador não encontrado: " + req.setorId()));
        TipoResiduo tipo = tipoRepository.findById(req.tipoId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de resíduo não encontrado: " + req.tipoId()));
        GrupoResiduo grupo = grupoRepository.findById(req.grupoId())
                .orElseThrow(() -> new EntityNotFoundException("Grupo de resíduo não encontrado: " + req.grupoId()));
        if (!tipo.getGrupo().getId().equals(grupo.getId())) {
            throw new IllegalArgumentException("O grupo informado não corresponde ao grupo do tipo de resíduo");
        }
        PesagemResiduo e = new PesagemResiduo();
        e.setTenant(tenant);
        e.setSetor(setor);
        e.setTipo(tipo);
        e.setGrupo(grupo);
        e.setDataHoraPesagem(req.dataHoraPesagem());
        e.setPesoKg(req.pesoKg());
        e.setTurno(req.turno());
        e.setRota(req.rota());
        e.setResponsavelNome(req.responsavelNome());
        e.setIdentificacaoBalanca(req.identificacaoBalanca());
        e.setObservacoes(req.observacoes());
        e.setStatus(StatusPesagem.REGISTRADA);
        return toDto(repository.save(e));
    }

    public PesagemResiduoDto validar(Long id) {
        PesagemResiduo e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setStatus(StatusPesagem.VALIDADA);
        return toDto(repository.save(e));
    }

    public PesagemResiduoDto cancelar(Long id) {
        PesagemResiduo e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setStatus(StatusPesagem.CANCELADA);
        return toDto(repository.save(e));
    }

    @Transactional(readOnly = true)
    public Page<PesagemResiduoDto> search(PesagemFiltroRequest filtro, Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (filtro != null && filtro.setorId() != null && filtro.dataInicio() != null && filtro.dataFim() != null) {
            LocalDateTime ini = filtro.dataInicio().atStartOfDay();
            LocalDateTime fim = filtro.dataFim().atTime(LocalTime.MAX);
            return repository.findAllByTenant_IdAndSetor_IdAndDataHoraPesagemBetween(
                    tenantId, filtro.setorId(), ini, fim, pageable).map(this::toDto);
        }
        return repository.findAllByTenant_Id(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public PesagemResiduoDto findById(Long id) {
        PesagemResiduo e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        return toDto(e);
    }

    private PesagemResiduo findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pesagem não encontrada: " + id));
    }

    public PesagemResiduoDto toDto(PesagemResiduo e) {
        return new PesagemResiduoDto(
                e.getId(),
                e.getTenant().getId(),
                e.getSetor().getId(),
                e.getSetor().getNome(),
                e.getTipo().getId(),
                e.getTipo().getNome(),
                e.getGrupo().getId(),
                e.getGrupo().getCodigo(),
                e.getDataHoraPesagem(),
                e.getPesoKg(),
                e.getTurno(),
                e.getRota(),
                e.getResponsavelNome(),
                e.getIdentificacaoBalanca(),
                e.getStatus(),
                e.getObservacoes()
        );
    }
}
