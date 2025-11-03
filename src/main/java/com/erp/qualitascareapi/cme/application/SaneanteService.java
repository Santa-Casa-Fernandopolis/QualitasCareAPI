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

    public SaneanteService(TenantRepository tenantRepository,
                           UserRepository userRepository,
                           SaneantePeraceticoLoteRepository saneanteRepository,
                           UsoSaneanteRepository usoSaneanteRepository) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.saneanteRepository = saneanteRepository;
        this.usoSaneanteRepository = usoSaneanteRepository;
    }

    public SaneanteLoteDto createLote(SaneanteLoteRequest request) {
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
        lote.setObservacoes(request.observacoes());
        SaneantePeraceticoLote saved = saneanteRepository.save(lote);
        return toDto(saved);
    }

    public Page<SaneanteLoteDto> listLotes(Pageable pageable) {
        return saneanteRepository.findAll(pageable).map(this::toDto);
    }

    private SaneanteLoteDto toDto(SaneantePeraceticoLote lote) {
        return new SaneanteLoteDto(lote.getId(), lote.getTenant().getId(), lote.getNumeroLote(),
                lote.getFabricante(), lote.getConcentracao(), lote.getDataValidade(),
                lote.getDataAbertura(), lote.getVolumeInicialMl(), lote.getObservacoes());
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
        return usoSaneanteRepository.findAll(pageable)
                .map(uso -> new UsoSaneanteDto(uso.getId(), uso.getLoteSaneante().getId(), uso.getDataUso(),
                        uso.getEtapa(), uso.getVolumeUtilizadoMl(), uso.getDiluicao(),
                        uso.getUsadoPor() != null ? uso.getUsadoPor().getId() : null,
                        uso.getObservacoes()));
    }
}
