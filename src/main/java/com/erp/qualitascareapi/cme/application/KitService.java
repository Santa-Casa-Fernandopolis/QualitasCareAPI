package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.domain.Instrumento;
import com.erp.qualitascareapi.cme.domain.KitItem;
import com.erp.qualitascareapi.cme.domain.KitProcedimento;
import com.erp.qualitascareapi.cme.domain.KitVersion;
import com.erp.qualitascareapi.cme.repo.InstrumentoRepository;
import com.erp.qualitascareapi.cme.repo.KitItemRepository;
import com.erp.qualitascareapi.cme.repo.KitProcedimentoRepository;
import com.erp.qualitascareapi.cme.repo.KitVersionRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class KitService {

    private final TenantRepository tenantRepository;
    private final InstrumentoRepository instrumentoRepository;
    private final KitProcedimentoRepository kitProcedimentoRepository;
    private final KitVersionRepository kitVersionRepository;
    private final KitItemRepository kitItemRepository;

    public KitService(TenantRepository tenantRepository,
                      InstrumentoRepository instrumentoRepository,
                      KitProcedimentoRepository kitProcedimentoRepository,
                      KitVersionRepository kitVersionRepository,
                      KitItemRepository kitItemRepository) {
        this.tenantRepository = tenantRepository;
        this.instrumentoRepository = instrumentoRepository;
        this.kitProcedimentoRepository = kitProcedimentoRepository;
        this.kitVersionRepository = kitVersionRepository;
        this.kitItemRepository = kitItemRepository;
    }

    public InstrumentoDto createInstrumento(InstrumentoRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        Instrumento instrumento = new Instrumento();
        instrumento.setTenant(tenant);
        instrumento.setNome(request.nome());
        instrumento.setCodigoHospitalar(request.codigoHospitalar());
        instrumento.setDescricao(request.descricao());
        Instrumento saved = instrumentoRepository.save(instrumento);
        return new InstrumentoDto(saved.getId(), tenant.getId(), saved.getNome(),
                saved.getCodigoHospitalar(), saved.getDescricao());
    }

    public Page<InstrumentoDto> listInstrumentos(Pageable pageable) {
        return instrumentoRepository.findAll(pageable)
                .map(i -> new InstrumentoDto(i.getId(), i.getTenant().getId(), i.getNome(),
                        i.getCodigoHospitalar(), i.getDescricao()));
    }

    public KitProcedimentoDto createKitProcedimento(KitProcedimentoRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        KitProcedimento kit = new KitProcedimento();
        kit.setTenant(tenant);
        kit.setNome(request.nome());
        kit.setCodigo(request.codigo());
        kit.setObservacoes(request.observacoes());
        kit.setAtivo(request.ativo() != null ? request.ativo() : Boolean.TRUE);
        KitProcedimento saved = kitProcedimentoRepository.save(kit);
        return new KitProcedimentoDto(saved.getId(), tenant.getId(), saved.getNome(), saved.getCodigo(),
                saved.getObservacoes(), saved.getAtivo());
    }

    public Page<KitProcedimentoDto> listKits(Pageable pageable) {
        return kitProcedimentoRepository.findAll(pageable)
                .map(k -> new KitProcedimentoDto(k.getId(), k.getTenant().getId(), k.getNome(), k.getCodigo(),
                        k.getObservacoes(), k.getAtivo()));
    }

    public KitVersionDto createKitVersion(KitVersionRequest request) {
        KitProcedimento kit = kitProcedimentoRepository.findById(request.kitId())
                .orElseThrow(() -> new EntityNotFoundException("Kit não encontrado"));
        KitVersion version = new KitVersion();
        version.setKit(kit);
        version.setNumeroVersao(request.numeroVersao());
        version.setVigenciaInicio(request.vigenciaInicio());
        version.setValidadeDias(request.validadeDias());
        version.setAtivo(request.ativo() != null ? request.ativo() : Boolean.TRUE);
        version.setObservacoes(request.observacoes());
        KitVersion saved = kitVersionRepository.save(version);
        return new KitVersionDto(saved.getId(), kit.getId(), saved.getNumeroVersao(), saved.getVigenciaInicio(),
                saved.getValidadeDias(), saved.getAtivo(), saved.getObservacoes());
    }

    public Page<KitVersionDto> listKitVersions(Pageable pageable) {
        return kitVersionRepository.findAll(pageable)
                .map(v -> new KitVersionDto(v.getId(), v.getKit().getId(), v.getNumeroVersao(),
                        v.getVigenciaInicio(), v.getValidadeDias(), v.getAtivo(), v.getObservacoes()));
    }

    public KitItemDto createKitItem(KitItemRequest request) {
        KitVersion versao = kitVersionRepository.findById(request.versaoId())
                .orElseThrow(() -> new EntityNotFoundException("Versão de kit não encontrada"));
        Instrumento instrumento = instrumentoRepository.findById(request.instrumentoId())
                .orElseThrow(() -> new EntityNotFoundException("Instrumento não encontrado"));
        KitItem item = new KitItem();
        item.setVersao(versao);
        item.setInstrumento(instrumento);
        item.setQuantidade(request.quantidade());
        item.setObservacoes(request.observacoes());
        KitItem saved = kitItemRepository.save(item);
        return new KitItemDto(saved.getId(), versao.getId(), instrumento.getId(), saved.getQuantidade(),
                saved.getObservacoes());
    }

    public Page<KitItemDto> listKitItems(Pageable pageable) {
        return kitItemRepository.findAll(pageable)
                .map(item -> new KitItemDto(item.getId(), item.getVersao().getId(),
                        item.getInstrumento().getId(), item.getQuantidade(), item.getObservacoes()));
    }
}
