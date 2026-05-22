package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.pgrss.api.dto.ColetaExternaDto;
import com.erp.qualitascareapi.pgrss.api.dto.ColetaExternaRequest;
import com.erp.qualitascareapi.pgrss.domain.ColetaExterna;
import com.erp.qualitascareapi.pgrss.domain.EmpresaColetora;
import com.erp.qualitascareapi.pgrss.domain.GrupoResiduo;
import com.erp.qualitascareapi.pgrss.enums.StatusColetaExterna;
import com.erp.qualitascareapi.pgrss.repo.ColetaExternaRepository;
import com.erp.qualitascareapi.pgrss.repo.EmpresaColetorRepository;
import com.erp.qualitascareapi.pgrss.repo.GrupoResiduoRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class ColetaExternaService {

    private final ColetaExternaRepository repository;
    private final EmpresaColetorRepository empresaRepository;
    private final GrupoResiduoRepository grupoRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public ColetaExternaService(ColetaExternaRepository repository,
                                 EmpresaColetorRepository empresaRepository,
                                 GrupoResiduoRepository grupoRepository,
                                 TenantRepository tenantRepository,
                                 TenantScopeGuard tenantScopeGuard) {
        this.repository = repository;
        this.empresaRepository = empresaRepository;
        this.grupoRepository = grupoRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public ColetaExternaDto registrar(ColetaExternaRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        Tenant tenant = tenantRepository.findById(req.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        EmpresaColetora empresa = empresaRepository.findById(req.empresaId())
                .orElseThrow(() -> new EntityNotFoundException("Empresa coletora não encontrada: " + req.empresaId()));
        if (empresa.getDataVencimentoLicenca().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Empresa coletora com licença vencida. Não é possível registrar coleta.");
        }
        GrupoResiduo grupo = grupoRepository.findById(req.grupoId())
                .orElseThrow(() -> new EntityNotFoundException("Grupo de resíduo não encontrado: " + req.grupoId()));
        ColetaExterna e = new ColetaExterna();
        e.setTenant(tenant);
        e.setEmpresa(empresa);
        e.setGrupo(grupo);
        e.setDataColeta(req.dataColeta());
        e.setPesoTotalKg(req.pesoTotalKg());
        e.setDestinacao(req.destinacao());
        e.setNumeroManifesto(req.numeroManifesto());
        e.setPlacaVeiculo(req.placaVeiculo());
        e.setNomeMotorista(req.nomeMotorista());
        e.setResponsavelNome(req.responsavelNome());
        e.setObservacoes(req.observacoes());
        e.setStatus(StatusColetaExterna.REGISTRADA);
        return toDto(repository.save(e));
    }

    public ColetaExternaDto marcarDocumentada(Long id) {
        ColetaExterna e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setStatus(StatusColetaExterna.DOCUMENTADA);
        return toDto(repository.save(e));
    }

    public ColetaExternaDto cancelar(Long id) {
        ColetaExterna e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setStatus(StatusColetaExterna.CANCELADA);
        return toDto(repository.save(e));
    }

    @Transactional(readOnly = true)
    public Page<ColetaExternaDto> search(Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return repository.findAllByTenant_Id(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public ColetaExternaDto findById(Long id) {
        ColetaExterna e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        return toDto(e);
    }

    private ColetaExterna findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coleta externa não encontrada: " + id));
    }

    private ColetaExternaDto toDto(ColetaExterna e) {
        return new ColetaExternaDto(
                e.getId(),
                e.getTenant().getId(),
                e.getEmpresa().getId(),
                e.getEmpresa().getRazaoSocial(),
                e.getGrupo().getId(),
                e.getGrupo().getCodigo(),
                e.getDataColeta(),
                e.getPesoTotalKg(),
                e.getDestinacao(),
                e.getNumeroManifesto(),
                e.getNumeroCertificadoDestinacao(),
                e.getPlacaVeiculo(),
                e.getNomeMotorista(),
                e.getResponsavelNome(),
                e.getStatus(),
                e.getObservacoes()
        );
    }
}
