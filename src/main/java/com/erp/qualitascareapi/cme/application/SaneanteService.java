package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.SaneanteLoteDto;
import com.erp.qualitascareapi.cme.api.dto.SaneanteLoteRequest;
import com.erp.qualitascareapi.cme.api.dto.UsoSaneanteDto;
import com.erp.qualitascareapi.cme.api.dto.UsoSaneanteRequest;
import com.erp.qualitascareapi.cme.domain.SaneantePeraceticoLote;
import com.erp.qualitascareapi.cme.domain.UsoSaneante;
import com.erp.qualitascareapi.cme.repo.SaneantePeraceticoLoteRepository;
import com.erp.qualitascareapi.cme.repo.UsoSaneanteRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SaneanteService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final SaneantePeraceticoLoteRepository saneanteRepository;
    private final UsoSaneanteRepository usoSaneanteRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public SaneanteService(TenantRepository tenantRepository,
                           UserRepository userRepository,
                           SaneantePeraceticoLoteRepository saneanteRepository,
                           UsoSaneanteRepository usoSaneanteRepository,
                           TenantScopeGuard tenantScopeGuard) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.saneanteRepository = saneanteRepository;
        this.usoSaneanteRepository = usoSaneanteRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public SaneanteLoteDto createLote(SaneanteLoteRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        SaneantePeraceticoLote lote = new SaneantePeraceticoLote();
        lote.setTenant(tenant);
        lote.setNumeroLote(request.numeroLote());
        lote.setFabricante(request.fabricante());
        lote.setConcentracao(request.concentracao());
        lote.setDataValidade(request.dataValidade());
        lote.setDataAbertura(request.dataAbertura());
        lote.setVolumeInicialMl(request.volumeInicialMl());
        lote.setPreparadoPor(resolvePreparadoPor(request.preparadoPorId(), request.tenantId()));
        lote.setObservacoes(request.observacoes());
        SaneantePeraceticoLote saved = saneanteRepository.save(lote);
        return toDto(saved);
    }

    public SaneanteLoteDto updateLote(Long id, SaneanteLoteRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        SaneantePeraceticoLote lote = saneanteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote de saneante não encontrado"));
        tenantScopeGuard.checkTenantAccess(lote.getTenant().getId());
        lote.setNumeroLote(request.numeroLote());
        lote.setFabricante(request.fabricante());
        lote.setConcentracao(request.concentracao());
        lote.setDataValidade(request.dataValidade());
        lote.setDataAbertura(request.dataAbertura());
        lote.setVolumeInicialMl(request.volumeInicialMl());
        lote.setPreparadoPor(resolvePreparadoPor(request.preparadoPorId(), request.tenantId()));
        lote.setObservacoes(request.observacoes());
        return toDto(saneanteRepository.save(lote));
    }

    public SaneanteLoteDto findLoteById(Long id) {
        return saneanteRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Lote de saneante não encontrado"));
    }

    public Page<SaneanteLoteDto> listLotes(Pageable pageable) {
        return saneanteRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable).map(this::toDto);
    }

    private SaneanteLoteDto toDto(SaneantePeraceticoLote lote) {
        Double volumeConsumidoMl = usoSaneanteRepository.sumVolumeUtilizadoByLoteId(lote.getId());
        User preparadoPor = lote.getPreparadoPor();
        return new SaneanteLoteDto(lote.getId(), lote.getTenant().getId(), lote.getNumeroLote(),
                lote.getFabricante(), lote.getConcentracao(), lote.getDataValidade(),
                lote.getDataAbertura(), lote.getVolumeInicialMl(), volumeConsumidoMl,
                preparadoPor != null ? preparadoPor.getId() : null,
                preparadoPor != null ? displayName(preparadoPor) : null,
                lote.getObservacoes());
    }

    private User resolvePreparadoPor(Long preparadoPorId, Long tenantId) {
        if (preparadoPorId == null) {
            return null;
        }
        User user = userRepository.findById(preparadoPorId)
                .orElseThrow(() -> new EntityNotFoundException("Profissional preparador não encontrado"));
        if (!user.getTenant().getId().equals(tenantId)) {
            throw new EntityNotFoundException("Profissional preparador não encontrado");
        }
        return user;
    }

    private String displayName(User user) {
        return user.getFullName() != null && !user.getFullName().isBlank()
                ? user.getFullName()
                : user.getUsername();
    }

    public UsoSaneanteDto registrarUso(UsoSaneanteRequest request) {
        SaneantePeraceticoLote lote = saneanteRepository.findById(request.loteSaneanteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote de saneante não encontrado"));
        UsoSaneante uso = new UsoSaneante();
        uso.setLoteSaneante(lote);
        uso.setDataUso(request.dataUso());
        uso.setEtapa(request.etapa());
        uso.setVolumeUtilizadoMl(request.volumeUtilizadoMl());
        uso.setDiluicao(request.diluicao());
        if (request.usadoPorId() != null) {
            User user = userRepository.findById(request.usadoPorId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
            uso.setUsadoPor(user);
        }
        uso.setObservacoes(request.observacoes());
        UsoSaneante saved = usoSaneanteRepository.save(uso);
        return new UsoSaneanteDto(saved.getId(), lote.getId(), saved.getDataUso(), saved.getEtapa(),
                saved.getVolumeUtilizadoMl(), saved.getDiluicao(),
                saved.getUsadoPor() != null ? saved.getUsadoPor().getId() : null,
                saved.getObservacoes());
    }

    public Page<UsoSaneanteDto> listUsos(Pageable pageable) {
        return usoSaneanteRepository.findAllByLoteSaneante_TenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(uso -> new UsoSaneanteDto(uso.getId(), uso.getLoteSaneante().getId(), uso.getDataUso(),
                        uso.getEtapa(), uso.getVolumeUtilizadoMl(), uso.getDiluicao(),
                        uso.getUsadoPor() != null ? uso.getUsadoPor().getId() : null,
                        uso.getObservacoes()));
    }
}
