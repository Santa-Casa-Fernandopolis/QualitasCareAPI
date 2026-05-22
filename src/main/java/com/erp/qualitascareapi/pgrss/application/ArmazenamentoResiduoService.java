package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.pgrss.api.dto.ArmazenamentoResiduoDto;
import com.erp.qualitascareapi.pgrss.api.dto.ArmazenamentoResiduoRequest;
import com.erp.qualitascareapi.pgrss.domain.ArmazenamentoResiduo;
import com.erp.qualitascareapi.pgrss.domain.GrupoResiduo;
import com.erp.qualitascareapi.pgrss.enums.StatusArmazenamento;
import com.erp.qualitascareapi.pgrss.repo.ArmazenamentoResiduoRepository;
import com.erp.qualitascareapi.pgrss.repo.GrupoResiduoRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class ArmazenamentoResiduoService {

    private final ArmazenamentoResiduoRepository repository;
    private final GrupoResiduoRepository grupoRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public ArmazenamentoResiduoService(ArmazenamentoResiduoRepository repository,
                                        GrupoResiduoRepository grupoRepository,
                                        TenantRepository tenantRepository,
                                        TenantScopeGuard tenantScopeGuard) {
        this.repository = repository;
        this.grupoRepository = grupoRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public ArmazenamentoResiduoDto registrar(ArmazenamentoResiduoRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        Tenant tenant = tenantRepository.findById(req.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        GrupoResiduo grupo = grupoRepository.findById(req.grupoId())
                .orElseThrow(() -> new EntityNotFoundException("Grupo de resíduo não encontrado: " + req.grupoId()));
        ArmazenamentoResiduo e = new ArmazenamentoResiduo();
        e.setTenant(tenant);
        e.setGrupo(grupo);
        e.setTipoArmazenamento(req.tipoArmazenamento());
        e.setDataHoraEntrada(req.dataHoraEntrada());
        e.setPesoEstimadoKg(req.pesoEstimadoKg());
        e.setResponsavelNome(req.responsavelNome());
        e.setLocalizacao(req.localizacao());
        e.setObservacoes(req.observacoes());
        e.setStatus(StatusArmazenamento.ARMAZENADO);
        return toDto(repository.save(e));
    }

    public ArmazenamentoResiduoDto remover(Long id) {
        ArmazenamentoResiduo e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setStatus(StatusArmazenamento.REMOVIDO);
        e.setDataHoraSaida(LocalDateTime.now());
        return toDto(repository.save(e));
    }

    public ArmazenamentoResiduoDto cancelar(Long id) {
        ArmazenamentoResiduo e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setStatus(StatusArmazenamento.CANCELADO);
        return toDto(repository.save(e));
    }

    @Transactional(readOnly = true)
    public Page<ArmazenamentoResiduoDto> search(Long grupoId, StatusArmazenamento status, Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (status != null) {
            return repository.findAllByTenant_IdAndStatus(tenantId, status).stream()
                    .filter(a -> grupoId == null || a.getGrupo().getId().equals(grupoId))
                    .map(this::toDto)
                    .collect(java.util.stream.Collectors.collectingAndThen(
                            java.util.stream.Collectors.toList(),
                            list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())));
        }
        return repository.findAllByTenant_Id(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public ArmazenamentoResiduoDto findById(Long id) {
        ArmazenamentoResiduo e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        return toDto(e);
    }

    private ArmazenamentoResiduo findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Armazenamento não encontrado: " + id));
    }

    private ArmazenamentoResiduoDto toDto(ArmazenamentoResiduo e) {
        return new ArmazenamentoResiduoDto(
                e.getId(),
                e.getTenant().getId(),
                e.getGrupo().getId(),
                e.getGrupo().getNome(),
                e.getTipoArmazenamento(),
                e.getDataHoraEntrada(),
                e.getDataHoraSaida(),
                e.getPesoEstimadoKg(),
                e.getResponsavelNome(),
                e.getLocalizacao(),
                e.getStatus(),
                e.getObservacoes()
        );
    }
}
