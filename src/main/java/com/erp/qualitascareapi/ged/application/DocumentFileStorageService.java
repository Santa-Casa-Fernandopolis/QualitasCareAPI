package com.erp.qualitascareapi.ged.application;

import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.repo.EvidenciaArquivoRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.Map;

@Service
public class DocumentFileStorageService {

    private final EvidenciaArquivoRepository evidenciaArquivoRepository;
    private final Path rootPath;

    public DocumentFileStorageService(EvidenciaArquivoRepository evidenciaArquivoRepository,
                                      @Value("${qualitascare.storage.documents-root:${user.home}/qualitascare/documentos}") String rootPath) {
        this.evidenciaArquivoRepository = evidenciaArquivoRepository;
        this.rootPath = Path.of(rootPath).toAbsolutePath().normalize();
    }

    @Transactional
    public EvidenciaArquivo store(Tenant tenant, Long documentId, Long versionId, MultipartFile file, User autor) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Arquivo obrigatório", Map.of("field", "file"));
        }

        String originalName = sanitize(file.getOriginalFilename());
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException ex) {
            throw new BadRequestException("Não foi possível ler o arquivo enviado", Map.of("file", originalName));
        }

        String hash = sha256(bytes);
        String tenantCode = sanitize(tenant.getCode() != null ? tenant.getCode() : String.valueOf(tenant.getId()));
        Path relativePath = Path.of(
                tenantCode,
                "documentos",
                String.valueOf(documentId),
                "versoes",
                String.valueOf(versionId),
                LocalDate.now() + "-" + hash.substring(0, 12) + "-" + originalName
        );
        Path target = rootPath.resolve(relativePath).normalize();
        if (!target.startsWith(rootPath)) {
            throw new BadRequestException("Nome de arquivo inválido", Map.of("file", originalName));
        }

        try {
            Files.createDirectories(target.getParent());
            Files.write(target, bytes);
        } catch (IOException ex) {
            throw new BadRequestException("Não foi possível armazenar o arquivo", Map.of("file", originalName));
        }

        EvidenciaArquivo evidencia = new EvidenciaArquivo();
        evidencia.setTenant(tenant);
        evidencia.setNomeArquivo(originalName);
        evidencia.setUri(relativePath.toString());
        evidencia.setHashSha256(hash);
        evidencia.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
        evidencia.setTamanhoBytes(file.getSize());
        evidencia.setAutor(autor);
        return evidenciaArquivoRepository.save(evidencia);
    }

    private String sanitize(String value) {
        String candidate = value == null || value.isBlank() ? "arquivo" : value.trim();
        return candidate.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String sha256(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 não disponível", ex);
        }
    }
}
