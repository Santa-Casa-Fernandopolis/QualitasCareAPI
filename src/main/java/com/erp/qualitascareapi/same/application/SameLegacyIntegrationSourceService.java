package com.erp.qualitascareapi.same.application;

import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.same.api.dto.SameLegacyConnectionTestDto;
import com.erp.qualitascareapi.same.api.dto.SameLegacyIntegrationSourceDto;
import com.erp.qualitascareapi.same.api.dto.SameLegacyIntegrationSourceRequest;
import com.erp.qualitascareapi.same.domain.SameLegacyIntegrationSource;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import com.erp.qualitascareapi.same.repo.SameLegacyIntegrationSourceRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Set;

@Service
@Transactional
public class SameLegacyIntegrationSourceService {

    private static final Set<SameSourceSystem> LEGACY_SOURCES = EnumSet.of(SameSourceSystem.WIRELINE, SameSourceSystem.SAVE);

    private final SameLegacyIntegrationSourceRepository repository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;
    private final TextEncryptor encryptor;

    public SameLegacyIntegrationSourceService(SameLegacyIntegrationSourceRepository repository,
                                              TenantRepository tenantRepository,
                                              TenantScopeGuard tenantScopeGuard,
                                              TextEncryptor encryptor) {
        this.repository = repository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
        this.encryptor = encryptor;
    }

    public SameLegacyIntegrationSourceDto create(SameLegacyIntegrationSourceRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        validateLegacySource(request.sourceSystem());

        SameLegacyIntegrationSource source = new SameLegacyIntegrationSource();
        source.setTenant(loadTenant(request.tenantId()));
        applyFields(source, request, true);
        return toDto(repository.save(source));
    }

    public SameLegacyIntegrationSourceDto update(Long id, SameLegacyIntegrationSourceRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        validateLegacySource(request.sourceSystem());

        SameLegacyIntegrationSource source = load(id);
        if (!source.getTenant().getId().equals(request.tenantId())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "same.legacy-source.tenant-change",
                    "Não é permitido alterar o tenant da fonte legada.");
        }
        applyFields(source, request, false);
        return toDto(repository.save(source));
    }

    public SameLegacyIntegrationSourceDto updateStatus(Long id, boolean active) {
        SameLegacyIntegrationSource source = load(id);
        source.setActive(active);
        return toDto(repository.save(source));
    }

    @Transactional(readOnly = true)
    public SameLegacyIntegrationSourceDto findById(Long id) {
        return toDto(load(id));
    }

    @Transactional(readOnly = true)
    public Page<SameLegacyIntegrationSourceDto> list(SameSourceSystem sourceSystem, Boolean active, Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (sourceSystem != null) {
            validateLegacySource(sourceSystem);
        }

        Page<SameLegacyIntegrationSource> page;
        if (sourceSystem != null && active != null) {
            page = repository.findAllByTenantIdAndSourceSystemAndActive(tenantId, sourceSystem, active, pageable);
        } else if (sourceSystem != null) {
            page = repository.findAllByTenantIdAndSourceSystem(tenantId, sourceSystem, pageable);
        } else if (active != null) {
            page = repository.findAllByTenantIdAndActive(tenantId, active, pageable);
        } else {
            page = repository.findAllByTenantId(tenantId, pageable);
        }
        return page.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public SameLegacyConnectionTestDto testConnection(Long id) {
        SameLegacyIntegrationSource source = load(id);
        try (Connection ignored = dataSource(source).getConnection()) {
            return new SameLegacyConnectionTestDto(true, "Conexão realizada com sucesso.");
        } catch (SQLException ex) {
            return new SameLegacyConnectionTestDto(false, "Não foi possível conectar à fonte legada: " + ex.getMessage());
        }
    }

    private void applyFields(SameLegacyIntegrationSource source,
                             SameLegacyIntegrationSourceRequest request,
                             boolean requirePassword) {
        source.setName(normalizeRequired(request.name()));
        source.setSourceSystem(request.sourceSystem());
        source.setJdbcUrl(normalizeRequired(request.jdbcUrl()));
        source.setUsername(normalizeNullable(request.username()));
        if (request.active() != null) {
            source.setActive(request.active());
        }

        String password = normalizeNullable(request.password());
        if (password != null && !"****".equals(password)) {
            source.setEncryptedPassword(encryptor.encrypt(password));
        } else if (requirePassword && source.getEncryptedPassword() == null) {
            source.setEncryptedPassword(null);
        }
    }

    private DriverManagerDataSource dataSource(SameLegacyIntegrationSource source) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(source.getJdbcUrl());
        dataSource.setUsername(source.getUsername());
        String password = decryptPassword(source);
        if (password != null) {
            dataSource.setPassword(password);
        }
        return dataSource;
    }

    private SameLegacyIntegrationSource load(Long id) {
        return repository.findByIdAndTenantId(id, tenantScopeGuard.currentTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Fonte legada SAME não encontrada"));
    }

    private Tenant loadTenant(Long tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
    }

    private void validateLegacySource(SameSourceSystem sourceSystem) {
        if (!LEGACY_SOURCES.contains(sourceSystem)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "same.legacy-source.invalid-system",
                    "Use esta configuração apenas para Wireline ou Save. O Soul MV é o sistema atual e já possui integração própria no módulo MV.");
        }
    }

    private SameLegacyIntegrationSourceDto toDto(SameLegacyIntegrationSource source) {
        return new SameLegacyIntegrationSourceDto(
                source.getId(),
                source.getTenant().getId(),
                source.getName(),
                source.getSourceSystem(),
                source.getJdbcUrl(),
                source.getUsername(),
                source.getEncryptedPassword() != null && !source.getEncryptedPassword().isBlank(),
                source.isActive(),
                source.getCreatedAt(),
                source.getUpdatedAt()
        );
    }

    private String decryptPassword(SameLegacyIntegrationSource source) {
        String encrypted = source.getEncryptedPassword();
        if (encrypted == null || encrypted.isBlank()) {
            return null;
        }
        try {
            return encryptor.decrypt(encrypted);
        } catch (Exception ignored) {
            return encrypted;
        }
    }

    private String normalizeRequired(String value) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "same.legacy-source.required",
                    "Preencha os dados obrigatórios da fonte legada.");
        }
        return normalized;
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}
