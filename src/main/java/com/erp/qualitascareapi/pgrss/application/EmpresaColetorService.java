package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.pgrss.api.dto.CustoTratamentoDto;
import com.erp.qualitascareapi.pgrss.api.dto.CustoTratamentoRequest;
import com.erp.qualitascareapi.pgrss.api.dto.EmpresaColetorDto;
import com.erp.qualitascareapi.pgrss.api.dto.EmpresaColetorRequest;
import com.erp.qualitascareapi.pgrss.domain.CustoTratamento;
import com.erp.qualitascareapi.pgrss.domain.EmpresaColetora;
import com.erp.qualitascareapi.pgrss.domain.TipoResiduo;
import com.erp.qualitascareapi.pgrss.enums.LicencaAmbientalStatus;
import com.erp.qualitascareapi.pgrss.repo.CustoTratamentoRepository;
import com.erp.qualitascareapi.pgrss.repo.EmpresaColetorRepository;
import com.erp.qualitascareapi.pgrss.repo.TipoResiduoRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EmpresaColetorService {

    private final EmpresaColetorRepository repository;
    private final CustoTratamentoRepository custoRepository;
    private final TipoResiduoRepository tipoResiduoRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public EmpresaColetorService(EmpresaColetorRepository repository,
                                 CustoTratamentoRepository custoRepository,
                                 TipoResiduoRepository tipoResiduoRepository,
                                 TenantRepository tenantRepository,
                                 TenantScopeGuard tenantScopeGuard) {
        this.repository = repository;
        this.custoRepository = custoRepository;
        this.tipoResiduoRepository = tipoResiduoRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    // ── EmpresaColetora ────────────────────────────────────────────────────

    public EmpresaColetorDto create(EmpresaColetorRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        Tenant tenant = loadTenant(req.tenantId());
        EmpresaColetora entity = new EmpresaColetora();
        applyRequest(entity, req, tenant);
        recalculateLicencaStatus(entity);
        return toDto(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<EmpresaColetorDto> list(Boolean ativo, Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (ativo != null) {
            return repository.findAllByTenant_IdAndAtivo(tenantId, ativo, pageable).map(this::toDto);
        }
        return repository.findAllByTenant_Id(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public EmpresaColetorDto findById(Long id) {
        return toDto(loadEntity(id));
    }

    public EmpresaColetorDto update(Long id, EmpresaColetorRequest req) {
        EmpresaColetora entity = loadEntity(id);
        tenantScopeGuard.checkRequestedTenant(entity.getTenant().getId());
        Tenant tenant = loadTenant(req.tenantId());
        applyRequest(entity, req, tenant);
        recalculateLicencaStatus(entity);
        return toDto(repository.save(entity));
    }

    public EmpresaColetorDto toggleAtivo(Long id, Boolean ativo) {
        EmpresaColetora entity = loadEntity(id);
        tenantScopeGuard.checkRequestedTenant(entity.getTenant().getId());
        entity.setAtivo(ativo);
        return toDto(repository.save(entity));
    }

    // ── CustoTratamento ────────────────────────────────────────────────────

    public CustoTratamentoDto createCusto(CustoTratamentoRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        Tenant tenant = loadTenant(req.tenantId());
        EmpresaColetora empresa = loadEntity(req.empresaColetorId());
        TipoResiduo tipo = tipoResiduoRepository.findById(req.tipoResiduoId())
                .filter(t -> t.getTenant().getId().equals(req.tenantId()))
                .orElseThrow(() -> new EntityNotFoundException("TipoResiduo não encontrado: " + req.tipoResiduoId()));

        CustoTratamento custo = new CustoTratamento();
        custo.setTenant(tenant);
        custo.setEmpresaColetora(empresa);
        custo.setTipoResiduo(tipo);
        custo.setValorPorKg(req.valorPorKg());
        custo.setMoeda(req.moeda() != null ? req.moeda() : "BRL");
        custo.setVigenciaInicio(req.vigenciaInicio());
        custo.setVigenciaFim(req.vigenciaFim());
        custo.setObservacoes(req.observacoes());
        return toCustoDto(custoRepository.save(custo));
    }

    @Transactional(readOnly = true)
    public List<CustoTratamentoDto> listCustosByEmpresa(Long empresaId) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return custoRepository.findAllByTenant_IdAndEmpresaColetora_Id(tenantId, empresaId)
                .stream().map(this::toCustoDto).toList();
    }

    public void deleteCusto(Long custoId) {
        CustoTratamento custo = custoRepository.findById(custoId)
                .orElseThrow(() -> new EntityNotFoundException("CustoTratamento não encontrado: " + custoId));
        tenantScopeGuard.checkRequestedTenant(custo.getTenant().getId());
        custoRepository.delete(custo);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void applyRequest(EmpresaColetora entity, EmpresaColetorRequest req, Tenant tenant) {
        entity.setTenant(tenant);
        entity.setRazaoSocial(req.razaoSocial());
        entity.setCnpj(req.cnpj());
        entity.setTipo(req.tipo());
        entity.setLicencaNumero(req.licencaNumero());
        entity.setLicencaVencimento(req.licencaVencimento());
        entity.setTelefone(req.telefone());
        entity.setEmail(req.email());
        entity.setResponsavelNome(req.responsavelNome());
        entity.setObservacoes(req.observacoes());
    }

    /** Calcula automaticamente o status da licença com base na data de vencimento. */
    private void recalculateLicencaStatus(EmpresaColetora entity) {
        if (entity.getLicencaVencimento() == null) {
            return;
        }
        LocalDate today = LocalDate.now();
        LocalDate vencimento = entity.getLicencaVencimento();
        if (vencimento.isBefore(today)) {
            entity.setLicencaStatus(LicencaAmbientalStatus.VENCIDA);
        } else if (vencimento.isBefore(today.plusDays(30))) {
            entity.setLicencaStatus(LicencaAmbientalStatus.PROXIMA_VENCIMENTO);
        } else {
            entity.setLicencaStatus(LicencaAmbientalStatus.ATIVA);
        }
    }

    private EmpresaColetora loadEntity(Long id) {
        EmpresaColetora entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("EmpresaColetora não encontrada: " + id));
        tenantScopeGuard.checkRequestedTenant(entity.getTenant().getId());
        return entity;
    }

    private Tenant loadTenant(Long tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado: " + tenantId));
    }

    EmpresaColetorDto toDto(EmpresaColetora e) {
        return new EmpresaColetorDto(
                e.getId(),
                e.getTenant().getId(),
                e.getRazaoSocial(),
                e.getCnpj(),
                e.getTipo(),
                e.getLicencaNumero(),
                e.getLicencaVencimento(),
                e.getLicencaStatus(),
                e.getTelefone(),
                e.getEmail(),
                e.getResponsavelNome(),
                e.getObservacoes(),
                e.getAtivo()
        );
    }

    CustoTratamentoDto toCustoDto(CustoTratamento c) {
        return new CustoTratamentoDto(
                c.getId(),
                c.getTenant().getId(),
                c.getEmpresaColetora().getId(),
                c.getEmpresaColetora().getRazaoSocial(),
                c.getTipoResiduo().getId(),
                c.getTipoResiduo().getNome(),
                c.getValorPorKg(),
                c.getMoeda(),
                c.getVigenciaInicio(),
                c.getVigenciaFim(),
                c.getObservacoes()
        );
    }
}
