package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.pgrss.api.dto.ColetaInternaDto;
import com.erp.qualitascareapi.pgrss.api.dto.ColetaInternaRequest;
import com.erp.qualitascareapi.pgrss.api.dto.PesagemResiduoDto;
import com.erp.qualitascareapi.pgrss.domain.ColetaInterna;
import com.erp.qualitascareapi.pgrss.domain.ColetaInternaItem;
import com.erp.qualitascareapi.pgrss.domain.PesagemResiduo;
import com.erp.qualitascareapi.pgrss.enums.StatusColetaInterna;
import com.erp.qualitascareapi.pgrss.repo.ColetaInternaItemRepository;
import com.erp.qualitascareapi.pgrss.repo.ColetaInternaRepository;
import com.erp.qualitascareapi.pgrss.repo.PesagemResiduoRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ColetaResiduoService {

    private final ColetaInternaRepository coletaRepository;
    private final ColetaInternaItemRepository itemRepository;
    private final PesagemResiduoRepository pesagemRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;
    private final PesagemResiduoService pesagemService;

    public ColetaResiduoService(ColetaInternaRepository coletaRepository,
                                 ColetaInternaItemRepository itemRepository,
                                 PesagemResiduoRepository pesagemRepository,
                                 TenantRepository tenantRepository,
                                 TenantScopeGuard tenantScopeGuard,
                                 PesagemResiduoService pesagemService) {
        this.coletaRepository = coletaRepository;
        this.itemRepository = itemRepository;
        this.pesagemRepository = pesagemRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
        this.pesagemService = pesagemService;
    }

    public ColetaInternaDto iniciarColeta(ColetaInternaRequest req) {
        tenantScopeGuard.checkRequestedTenant(req.tenantId());
        Tenant tenant = tenantRepository.findById(req.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        ColetaInterna e = new ColetaInterna();
        e.setTenant(tenant);
        e.setDataHoraColeta(req.dataHoraColeta());
        e.setNomeRota(req.nomeRota());
        e.setResponsavelNome(req.responsavelNome());
        e.setObservacoes(req.observacoes());
        e.setStatus(StatusColetaInterna.INICIADA);
        return toDto(coletaRepository.save(e), List.of());
    }

    public ColetaInternaDto vincularPesagem(Long coletaId, Long pesagemId) {
        ColetaInterna coleta = findColetaEntity(coletaId);
        tenantScopeGuard.checkRequestedTenant(coleta.getTenant().getId());
        if (itemRepository.existsByPesagem_Id(pesagemId)) {
            throw new IllegalStateException("Pesagem já está vinculada a uma coleta interna");
        }
        PesagemResiduo pesagem = pesagemRepository.findById(pesagemId)
                .orElseThrow(() -> new EntityNotFoundException("Pesagem não encontrada: " + pesagemId));
        ColetaInternaItem item = new ColetaInternaItem();
        item.setColetaInterna(coleta);
        item.setPesagem(pesagem);
        itemRepository.save(item);
        return findById(coletaId);
    }

    public ColetaInternaDto finalizarColeta(Long id) {
        ColetaInterna e = findColetaEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setStatus(StatusColetaInterna.FINALIZADA);
        coletaRepository.save(e);
        return findById(id);
    }

    public ColetaInternaDto cancelarColeta(Long id) {
        ColetaInterna e = findColetaEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        e.setStatus(StatusColetaInterna.CANCELADA);
        coletaRepository.save(e);
        return findById(id);
    }

    @Transactional(readOnly = true)
    public ColetaInternaDto findById(Long id) {
        ColetaInterna e = findColetaEntity(id);
        tenantScopeGuard.checkRequestedTenant(e.getTenant().getId());
        List<PesagemResiduoDto> pesagens = itemRepository.findAllByColetaInterna_Id(id).stream()
                .map(item -> pesagemService.toDto(item.getPesagem()))
                .toList();
        return toDto(e, pesagens);
    }

    @Transactional(readOnly = true)
    public Page<ColetaInternaDto> findAll(Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return coletaRepository.findAllByTenant_Id(tenantId, pageable)
                .map(c -> toDto(c, List.of()));
    }

    private ColetaInterna findColetaEntity(Long id) {
        return coletaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coleta interna não encontrada: " + id));
    }

    private ColetaInternaDto toDto(ColetaInterna e, List<PesagemResiduoDto> pesagens) {
        return new ColetaInternaDto(
                e.getId(),
                e.getTenant().getId(),
                e.getDataHoraColeta(),
                e.getNomeRota(),
                e.getResponsavelNome(),
                e.getStatus(),
                e.getObservacoes(),
                pesagens
        );
    }
}
