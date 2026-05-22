package com.erp.qualitascareapi.same.storage;

import com.erp.qualitascareapi.common.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class LocalSameFileStorageService implements SameFileStorageService {

    private final Path storageRoot;

    public LocalSameFileStorageService(@Value("${same.storage.path:/data/ged-same/documents}") String storagePath) {
        this.storageRoot = Paths.get(storagePath).toAbsolutePath().normalize();
    }

    @Override
    public SameStoredFile storePdf(MultipartFile file, Long tenantId, Long patientMasterId) {
        if (file == null || file.isEmpty()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "same.document.file-empty",
                    "Informe um arquivo PDF para anexar ao prontuário.");
        }

        String technicalName = "same-doc-" + tenantId + "-" + patientMasterId + "-" + UUID.randomUUID() + ".pdf";
        Path relativeDir = Paths.get("tenant-" + tenantId, "patient-" + patientMasterId);
        Path targetDir = storageRoot.resolve(relativeDir).normalize();
        Path target = targetDir.resolve(technicalName).normalize();

        try {
            Files.createDirectories(targetDir);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream in = file.getInputStream();
                 DigestInputStream digestInput = new DigestInputStream(in, digest)) {
                Files.copy(digestInput, target);
            }
            String hash = HexFormat.of().formatHex(digest.digest());
            String relativePath = relativeDir.resolve(technicalName).toString();
            return new SameStoredFile(
                    technicalName,
                    relativePath,
                    hash,
                    file.getContentType() != null ? file.getContentType() : "application/pdf",
                    file.getSize()
            );
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao gravar PDF SAME: " + technicalName, e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível", e);
        }
    }

    @Override
    public Resource load(String filePath) {
        try {
            Path target = storageRoot.resolve(filePath).normalize();
            if (!target.startsWith(storageRoot)) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "same.document.invalid-path",
                        "Caminho de arquivo inválido.");
            }
            Resource resource = new UrlResource(target.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ApplicationException(HttpStatus.NOT_FOUND, "same.document.file-not-found",
                        "Arquivo PDF não encontrado no armazenamento.");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "same.document.invalid-path",
                    "Caminho de arquivo inválido.", e);
        }
    }
}
