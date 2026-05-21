package com.erp.qualitascareapi.environmental.repo;

import com.erp.qualitascareapi.environmental.domain.DispositivoIoT;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DispositivoIoTRepository extends JpaRepository<DispositivoIoT, Long> {
    Page<DispositivoIoT> findAllByTenantId(Long tenantId, Pageable pageable);
    Optional<DispositivoIoT> findByApiKey(String apiKey);
    Optional<DispositivoIoT> findByApiKeyAndAtivoTrue(String apiKey);
    boolean existsByDeviceId(String deviceId);
}
