package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.pgrss.api.dto.DocumentoResiduoDto;
import com.erp.qualitascareapi.pgrss.domain.ColetaExterna;
import com.erp.qualitascareapi.pgrss.domain.DocumentoResiduo;
import com.erp.qualitascareapi.pgrss.enums.TipoDocumentoResiduo;
import com.erp.qualitascareapi.pgrss.repo.ColetaExternaRepository;
import com.erp.qualitascareapi.pgrss.repo.DocumentoResiduoRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

@Service
@Transactional
public class DocumentoResiduoService {

    private final DocumentoResiduoRepository repository;
    private final ColetaExternaRepository coletaExternaRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;
    private final FileStorageService fileStorageService;

    public DocumentoResiduoService(DocumentoResiduoRepository repository,
                                    ColetaExternaRepository coletaExternaRepository,
                                    TenantRepository tenantRepository,
                                    TenantScopeGuard tenantScopeGuard,
                                    FileStorageService fileStorageService) {
        this.repository = repository;
        this.coletaExternaRepository = coletaExternaRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
        this.fileStorageService = fileStorageService;
    }

    public DocumentoResiduoDto upload(Long coletaExternaId, TipoDocumentoResiduo tipoDocumento,
                                       String nomeArquivo, String mimeType, byte[] data,
                                       String uploadadoPorNome) {
        ColetaExterna coleta = coletaExternaRepository.findById(coletaExternaId)
                .orElseThrow(() -> new EntityNotFoundException("Coleta externa não encontrada: " + coletaExternaId));
        tenantScopeGuard.checkRequestedTenant(coleta.getTenant().getId());
        Tenant tenant = tenantRepository.findById(coleta.getTenant().getId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));

        String hash = computeSha256(data);
        String subPath = "coletas-externas/" + coletaExternaId;
        String storedPath = fileStorageService.store(subPath, nomeArquivo, data);

        DocumentoResiduo e = new DocumentoResiduo();
        e.setTenant(tenant);
        e.setColetaExterna(coleta);
        e.setTipoDocumento(tipoDocumento);
        e.setNomeArquivo(nomeArquivo);
        e.setCaminhoArquivo(storedPath);
        e.setHashSha256(hash);
        e.setMimeType(mimeType);
        e.setTamanhoBytes((long) data.length);
        e.setUploadadoPorNome(uploadadoPorNome);
        e.setUploadadoEm(LocalDateTime.now());
        e.setAtivo(true);
        return toDto(repository.save(e));
    }

    @Transactional(readOnly = true)
    public List<DocumentoResiduoDto> listByColetaExterna(Long coletaExternaId) {
        ColetaExterna coleta = coletaExternaRepository.findById(coletaExternaId)
                .orElseThrow(() -> new EntityNotFoundException("Coleta externa não encontrada: " + coletaExternaId));
        tenantScopeGuard.checkRequestedTenant(coleta.getTenant().getId());
        return repository.findAllByColetaExterna_IdAndAtivoTrue(coletaExternaId)
                .stream().map(this::toDto).toList();
    }

    private String computeSha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data);
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private DocumentoResiduoDto toDto(DocumentoResiduo e) {
        return new DocumentoResiduoDto(
                e.getId(),
                e.getColetaExterna() != null ? e.getColetaExterna().getId() : null,
                e.getTipoDocumento(),
                e.getNomeArquivo(),
                e.getMimeType(),
                e.getTamanhoBytes(),
                e.getUploadadoPorNome(),
                e.getUploadadoEm(),
                e.isAtivo()
        );
    }
}
