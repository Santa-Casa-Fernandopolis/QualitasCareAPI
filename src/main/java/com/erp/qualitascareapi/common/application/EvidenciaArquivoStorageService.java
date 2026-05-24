package com.erp.qualitascareapi.common.application;

import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.repo.EvidenciaArquivoRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.Map;
import java.util.Set;

@Service
public class EvidenciaArquivoStorageService {

    private static final long MAX_IMAGE_SIZE_BYTES = 10L * 1024L * 1024L;
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final EvidenciaArquivoRepository evidenciaArquivoRepository;
    private final Path rootPath;

    public EvidenciaArquivoStorageService(EvidenciaArquivoRepository evidenciaArquivoRepository,
                                          @Value("${qualitascare.storage.evidence-root:${user.home}/qualitascare/evidencias}") String rootPath) {
        this.evidenciaArquivoRepository = evidenciaArquivoRepository;
        this.rootPath = Path.of(rootPath).toAbsolutePath().normalize();
    }

    @Transactional
    public EvidenciaArquivo storeImage(Tenant tenant, MultipartFile file, User autor, String scope) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Imagem obrigatória", Map.of("field", "file"));
        }
        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new BadRequestException("A imagem deve ter até 10 MB", Map.of("field", "file"));
        }

        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BadRequestException("Envie uma imagem JPG, PNG ou WebP", Map.of("contentType", contentType));
        }

        String originalName = sanitize(file.getOriginalFilename());
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException ex) {
            throw new BadRequestException("Não foi possível ler a imagem enviada", Map.of("file", originalName));
        }

        String hash = sha256(bytes);
        String tenantCode = sanitize(tenant.getCode() != null ? tenant.getCode() : String.valueOf(tenant.getId()));
        String normalizedScope = sanitize(scope == null || scope.isBlank() ? "geral" : scope);
        Path relativePath = Path.of(
                tenantCode,
                normalizedScope,
                LocalDate.now().toString(),
                hash.substring(0, 12) + "-" + originalName
        );
        Path target = rootPath.resolve(relativePath).normalize();
        if (!target.startsWith(rootPath)) {
            throw new BadRequestException("Nome de arquivo inválido", Map.of("file", originalName));
        }

        try {
            Files.createDirectories(target.getParent());
            Files.write(target, bytes);
        } catch (IOException ex) {
            throw new BadRequestException("Não foi possível armazenar a imagem", Map.of("file", originalName));
        }

        EvidenciaArquivo evidencia = new EvidenciaArquivo();
        evidencia.setTenant(tenant);
        evidencia.setNomeArquivo(originalName);
        evidencia.setUri(relativePath.toString());
        evidencia.setHashSha256(hash);
        evidencia.setContentType(contentType);
        evidencia.setTamanhoBytes(file.getSize());
        evidencia.setAutor(autor);
        return evidenciaArquivoRepository.save(evidencia);
    }

    public Resource loadAsResource(EvidenciaArquivo evidencia) {
        Path file = rootPath.resolve(evidencia.getUri()).normalize();
        if (!file.startsWith(rootPath)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "evidence.path.invalid", "Caminho de evidência inválido.");
        }
        try {
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ApplicationException(HttpStatus.NOT_FOUND, "evidence.file.not-found", "Arquivo de evidência não encontrado.");
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "evidence.path.invalid", "Caminho de evidência inválido.", ex);
        }
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
