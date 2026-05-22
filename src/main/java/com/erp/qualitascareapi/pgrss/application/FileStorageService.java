package com.erp.qualitascareapi.pgrss.application;

public interface FileStorageService {

    /**
     * Stores the given file data at the specified subpath and filename.
     *
     * @param subPath  relative sub-directory within the storage root (e.g. "pgrss/coletas")
     * @param filename the file name to use
     * @param data     raw file bytes
     * @return the stored file path (relative to storage root)
     */
    String store(String subPath, String filename, byte[] data);
}
