package com.erp.qualitascareapi.environmental.repo;

import com.erp.qualitascareapi.environmental.domain.DispositivoIoT;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DispositivoIoTRepository extends JpaRepository<DispositivoIoT, Long> {

    Optional<DispositivoIoT> findByIdAndTenantId(Long id, Long tenantId);

    Page<DispositivoIoT> findAllByTenantId(Long tenantId, Pageable pageable);

    Optional<DispositivoIoT> findByApiKey(String apiKey);

    Optional<DispositivoIoT> findByApiKeyAndAtivoTrue(String apiKey);

    boolean existsByDeviceId(String deviceId);

    long countByTenantIdAndAtivo(Long tenantId, boolean ativo);

    /**
     * Conta dispositivos ativos que estão offline:
     * nunca enviaram leitura (ultimaLeitura IS NULL) ou
     * a última leitura é anterior ao limite informado.
     */
    @Query("""
            SELECT COUNT(d) FROM DispositivoIoT d
            WHERE d.tenant.id = :tenantId
              AND d.ativo = true
              AND (d.ultimaLeitura IS NULL OR d.ultimaLeitura < :limite)
            """)
    long countOffline(@Param("tenantId") Long tenantId, @Param("limite") LocalDateTime limite);
}
