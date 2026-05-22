package com.erp.qualitascareapi.same.storage;

public record SameStoredFile(
        String fileName,
        String filePath,
        String fileHashSha256,
        String mimeType,
        long fileSize
) {
}
