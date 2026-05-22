package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.pgrss.api.dto.CustoTratamentoDto;
import com.erp.qualitascareapi.pgrss.api.dto.CustoTratamentoRequest;
import com.erp.qualitascareapi.pgrss.domain.CustoTratamento;
import com.erp.qualitascareapi.pgrss.domain.GrupoResiduo;
import com.erp.qualitascareapi.pgrss.repo.CustoTratamentoRepository;
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
public class CustoTratamentoService {

    private final CustoTratamentoRepository repository;
    private final GrupoResiduoRepository grupoRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public CustoTratamentoService(CustoTratamentoRepository repository,
                                   GrupoResiduoRepository grupoRepository,
                                   TenantRepository tenantRepository,
                                   TenantScopeGuard tenantScopeGuard) {
        this.repository = repository;
        this.grupoRepository = grupoRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public CustoTratamentoDto create(CustoTratamentoRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        Tenant tenant = tenantRepository.findById(req.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        GrupoResiduo grupo = grupoRepository.findById(req.grupoId())
                .orElseThrow(() -> new EntityNotFoundException("Grupo de resíduo não encontrado: " + req.grupoId()));
        if (!grupo.getTenant().getId().equals(req.tenantId())) {
            throw new IllegalStateException("Grupo não pertence ao tenant informado");
        }
        CustoTratamento e = new CustoTratamento();
        e.setTenant(tenant);
        e.setGrupo(grupo);
        e.setCustoPorKg(req.custoPorKg());
        e.setDataInicioVigencia(req.dataInicioVigencia());
        e.setDataFimVigencia(req.dataFimVigencia());
        e.setAtivo(true);
        return toDto(repository.save(e));
    }

    @Transactional(readOnly = true)
    public Page<CustoTratamentoDto> listAll(Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return repository.findAllByTenant_Id(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<CustoTratamentoDto> findVigenteByGrupo(Long grupoId) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return repository.findAllByTenant_IdAndGrupo_IdAndAtivoTrue(tenantId, grupoId)
                .stream().map(this::toDto).toList();
    }

    private CustoTratamentoDto toDto(CustoTratamento e) {
        return new CustoTratamentoDto(
                e.getId(),
                e.getTenant().getId(),
                e.getGrupo().getId(),
                e.getGrupo().getNome(),
                e.getCustoPorKg(),
                e.getDataInicioVigencia(),
                e.getDataFimVigencia(),
                e.isAtivo()
        );
    }
}
