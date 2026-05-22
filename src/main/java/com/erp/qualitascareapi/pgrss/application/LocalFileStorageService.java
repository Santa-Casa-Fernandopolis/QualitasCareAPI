package com.erp.qualitascareapi.pgrss.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final String storagePath;

    public LocalFileStorageService(@Value("${pgrss.storage.path:/tmp/pgrss-storage}") String storagePath) {
        this.storagePath = storagePath;
    }

    @Override
    public String store(String subPath, String filename, byte[] data) {
        try {
            Path dir = Paths.get(storagePath, subPath);
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            Files.write(target, data);
            return Paths.get(subPath, filename).toString();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store file: " + filename, e);
        }
    }
}
