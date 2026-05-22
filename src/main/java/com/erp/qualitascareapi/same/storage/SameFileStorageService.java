package com.erp.qualitascareapi.same.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface SameFileStorageService {

    SameStoredFile storePdf(MultipartFile file, Long tenantId, Long patientMasterId);

    Resource load(String filePath);
}
