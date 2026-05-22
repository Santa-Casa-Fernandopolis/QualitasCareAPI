package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.pgrss.api.dto.EmpresaColetorDto;
import com.erp.qualitascareapi.pgrss.api.dto.EmpresaColetorRequest;
import com.erp.qualitascareapi.pgrss.domain.EmpresaColetora;
import com.erp.qualitascareapi.pgrss.repo.EmpresaColetorRepository;
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
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public EmpresaColetorService(EmpresaColetorRepository repository,
                                  TenantRepository tenantRepository,
                                  TenantScopeGuard tenantScopeGuard) {
        this.repository = repository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public EmpresaColetorDto create(EmpresaColetorRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        Tenant tenant = tenantRepository.findById(req.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        EmpresaColetora e = new EmpresaColetora();
        e.setTenant(tenant);
        applyFields(e, req);
        e.setAtivo(true);
        return toDto(repository.save(e));
    }

    public EmpresaColetorDto update(Long id, EmpresaColetorRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        EmpresaColetora e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        applyFields(e, req);
        return toDto(repository.save(e));
    }

    public EmpresaColetorDto toggleAtivo(Long id) {
        EmpresaColetora e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setAtivo(!e.isAtivo());
        return toDto(repository.save(e));
    }

    @Transactional(readOnly = true)
    public Page<EmpresaColetorDto> findAll(Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return repository.findAllByTenant_Id(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public EmpresaColetorDto findById(Long id) {
        EmpresaColetora e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        return toDto(e);
    }

    @Transactional(readOnly = true)
    public List<EmpresaColetorDto> findLicencasVencidas() {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return repository.findAllByTenant_IdAndAtivoTrueAndDataVencimentoLicencaBefore(tenantId, LocalDate.now())
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EmpresaColetorDto> findLicencasProximasVencimento(int dias) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(dias);
        return repository.findAllByTenant_IdAndAtivoTrueAndDataVencimentoLicencaBetween(tenantId, hoje, limite)
                .stream().map(this::toDto).toList();
    }

    private EmpresaColetora findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa coletora não encontrada: " + id));
    }

    private void applyFields(EmpresaColetora e, EmpresaColetorRequest req) {
        e.setRazaoSocial(req.razaoSocial());
        e.setNomeFantasia(req.nomeFantasia());
        e.setCnpj(req.cnpj());
        e.setNumeroLicenca(req.numeroLicenca());
        e.setDataVencimentoLicenca(req.dataVencimentoLicenca());
        e.setNomeContato(req.nomeContato());
        e.setTelefone(req.telefone());
        e.setEmail(req.email());
    }

    private EmpresaColetorDto toDto(EmpresaColetora e) {
        return new EmpresaColetorDto(
                e.getId(),
                e.getTenant().getId(),
                e.getRazaoSocial(),
                e.getNomeFantasia(),
                e.getCnpj(),
                e.getNumeroLicenca(),
                e.getDataVencimentoLicenca(),
                e.getNomeContato(),
                e.getTelefone(),
                e.getEmail(),
                e.isAtivo()
        );
    }
}
