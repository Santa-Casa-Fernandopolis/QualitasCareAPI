package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.pgrss.api.dto.PlanoAcaoResiduoDto;
import com.erp.qualitascareapi.pgrss.api.dto.PlanoAcaoResiduoRequest;
import com.erp.qualitascareapi.pgrss.domain.NaoConformidadeResiduo;
import com.erp.qualitascareapi.pgrss.domain.PlanoAcaoResiduo;
import com.erp.qualitascareapi.pgrss.enums.StatusPlanoAcao;
import com.erp.qualitascareapi.pgrss.repo.NaoConformidadeResiduoRepository;
import com.erp.qualitascareapi.pgrss.repo.PlanoAcaoResiduoRepository;
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
public class PlanoAcaoResiduoService {

    private final PlanoAcaoResiduoRepository repository;
    private final NaoConformidadeResiduoRepository ncRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public PlanoAcaoResiduoService(PlanoAcaoResiduoRepository repository,
                                    NaoConformidadeResiduoRepository ncRepository,
                                    TenantScopeGuard tenantScopeGuard) {
        this.repository = repository;
        this.ncRepository = ncRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public PlanoAcaoResiduoDto criar(Long naoConformidadeId, PlanoAcaoResiduoRequest req) {
        NaoConformidadeResiduo nc = ncRepository.findById(naoConformidadeId)
                .orElseThrow(() -> new EntityNotFoundException("Não conformidade não encontrada: " + naoConformidadeId));
        tenantScopeGuard.checkRequestedTenant(nc.getTenant().getId());
        Tenant tenant = nc.getTenant();
        PlanoAcaoResiduo e = new PlanoAcaoResiduo();
        e.setTenant(tenant);
        e.setNaoConformidade(nc);
        e.setDescricaoAcao(req.descricaoAcao());
        e.setResponsavelNome(req.responsavelNome());
        e.setDataPrazo(req.dataPrazo());
        e.setStatus(StatusPlanoAcao.ABERTO);
        return toDto(repository.save(e));
    }

    public PlanoAcaoResiduoDto concluir(Long id, String descricaoEvidencia) {
        PlanoAcaoResiduo e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setStatus(StatusPlanoAcao.CONCLUIDO);
        e.setDataConclusao(LocalDate.now());
        e.setDescricaoEvidencia(descricaoEvidencia);
        return toDto(repository.save(e));
    }

    public PlanoAcaoResiduoDto cancelar(Long id) {
        PlanoAcaoResiduo e = findEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setStatus(StatusPlanoAcao.CANCELADO);
        return toDto(repository.save(e));
    }

    @Transactional(readOnly = true)
    public Page<PlanoAcaoResiduoDto> search(Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return repository.findAllByTenant_Id(tenantId, pageable).map(this::toDto);
    }

    public int verificarVencidos() {
        Long tenantId = tenantScopeGuard.currentTenantId();
        List<PlanoAcaoResiduo> vencidos = repository.findAllByTenant_IdAndDataPrazoBefore(tenantId, LocalDate.now())
                .stream()
                .filter(p -> p.getStatus() == StatusPlanoAcao.ABERTO || p.getStatus() == StatusPlanoAcao.EM_ANDAMENTO)
                .toList();
        vencidos.forEach(p -> p.setStatus(StatusPlanoAcao.VENCIDO));
        repository.saveAll(vencidos);
        return vencidos.size();
    }

    private PlanoAcaoResiduo findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plano de ação não encontrado: " + id));
    }

    private PlanoAcaoResiduoDto toDto(PlanoAcaoResiduo e) {
        return new PlanoAcaoResiduoDto(
                e.getId(),
                e.getTenant().getId(),
                e.getNaoConformidade().getId(),
                e.getDescricaoAcao(),
                e.getResponsavelNome(),
                e.getDataPrazo(),
                e.getDataConclusao(),
                e.getStatus(),
                e.getDescricaoEvidencia()
        );
    }
}
