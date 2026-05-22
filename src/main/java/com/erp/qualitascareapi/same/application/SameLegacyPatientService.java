package com.erp.qualitascareapi.same.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.same.api.dto.SameLegacyPatientSnapshotDto;
import com.erp.qualitascareapi.same.domain.SameLegacyPatientSnapshot;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import com.erp.qualitascareapi.same.legacy.LegacyPatientConnector;
import com.erp.qualitascareapi.same.legacy.LegacyPatientRecord;
import com.erp.qualitascareapi.same.repo.SameLegacyPatientSnapshotRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class SameLegacyPatientService {

    private final List<LegacyPatientConnector> connectors;
    private final SameLegacyPatientSnapshotRepository snapshotRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;
    private final ObjectMapper objectMapper;

    public SameLegacyPatientService(List<LegacyPatientConnector> connectors,
                                    SameLegacyPatientSnapshotRepository snapshotRepository,
                                    TenantRepository tenantRepository,
                                    TenantScopeGuard tenantScopeGuard,
                                    ObjectMapper objectMapper) {
        this.connectors = connectors;
        this.snapshotRepository = snapshotRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
        this.objectMapper = objectMapper;
    }

    public List<SameLegacyPatientSnapshotDto> searchByCpf(SameSourceSystem sourceSystem, String cpf) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return connector(sourceSystem).searchByCpf(tenantId, onlyDigits(cpf))
                .stream()
                .map(record -> persistSnapshot(record, tenantId))
                .map(this::toDto)
                .toList();
    }

    public List<SameLegacyPatientSnapshotDto> searchByMedicalRecordCode(SameSourceSystem sourceSystem, String code) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return connector(sourceSystem).searchByMedicalRecordCode(tenantId, sourceSystem, normalizeCode(code))
                .stream()
                .map(record -> persistSnapshot(record, tenantId))
                .map(this::toDto)
                .toList();
    }

    public List<SameLegacyPatientSnapshotDto> searchByNameAndBirthDate(SameSourceSystem sourceSystem,
                                                                       String name,
                                                                       LocalDate birthDate) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        String likeName = name == null ? null : "%" + name.trim() + "%";
        return connector(sourceSystem).searchByNameAndBirthDate(tenantId, likeName, birthDate)
                .stream()
                .map(record -> persistSnapshot(record, tenantId))
                .map(this::toDto)
                .toList();
    }

    public SameLegacyPatientSnapshotDto getByExternalPatientId(SameSourceSystem sourceSystem, String externalPatientId) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return connector(sourceSystem).getByExternalPatientId(tenantId, externalPatientId)
                .map(record -> persistSnapshot(record, tenantId))
                .map(this::toDto)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "same.legacy-patient.not-found",
                        "Paciente não encontrado na fonte informada."));
    }

    @Transactional(readOnly = true)
    public Page<SameLegacyPatientSnapshotDto> listSnapshots(SameSourceSystem sourceSystem,
                                                            String cpf,
                                                            String medicalRecordCode,
                                                            Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        String normalizedCpf = onlyDigits(cpf);
        String normalizedCode = normalizeCode(medicalRecordCode);

        Page<SameLegacyPatientSnapshot> page;
        if (sourceSystem != null && normalizedCode != null) {
            page = snapshotRepository.findAllByTenantIdAndSourceSystemAndMedicalRecordCode(
                    tenantId, sourceSystem, normalizedCode, pageable);
        } else if (sourceSystem != null && normalizedCpf != null) {
            page = snapshotRepository.findAllByTenantIdAndSourceSystemAndCpf(tenantId, sourceSystem, normalizedCpf, pageable);
        } else if (sourceSystem != null) {
            page = snapshotRepository.findAllByTenantIdAndSourceSystem(tenantId, sourceSystem, pageable);
        } else if (normalizedCpf != null) {
            page = snapshotRepository.findAllByTenantIdAndCpf(tenantId, normalizedCpf, pageable);
        } else {
            page = snapshotRepository.findAllByTenantId(tenantId, pageable);
        }
        return page.map(this::toDto);
    }

    private SameLegacyPatientSnapshot persistSnapshot(LegacyPatientRecord record, Long tenantId) {
        SameLegacyPatientSnapshot snapshot = new SameLegacyPatientSnapshot();
        snapshot.setTenant(loadTenant(tenantId));
        snapshot.setSourceSystem(record.sourceSystem());
        snapshot.setExternalPatientId(normalizeNullable(record.externalPatientId()));
        snapshot.setMedicalRecordCode(normalizeCode(record.medicalRecordCode()));
        snapshot.setFullName(normalizeNullable(record.fullName()));
        snapshot.setMotherName(normalizeNullable(record.motherName()));
        snapshot.setBirthDate(record.birthDate());
        snapshot.setCpf(onlyDigits(record.cpf()));
        snapshot.setCns(onlyDigits(record.cns()));
        snapshot.setSex(record.sex());
        snapshot.setRawPayloadJson(record.rawPayloadJson() != null ? record.rawPayloadJson() : toJson(record));
        return snapshotRepository.save(snapshot);
    }

    private LegacyPatientConnector connector(SameSourceSystem sourceSystem) {
        if (sourceSystem == null || sourceSystem == SameSourceSystem.MANUAL) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "same.legacy-patient.invalid-source",
                    "Informe uma fonte válida: SOUL_MV, WIRELINE ou SAVE.");
        }
        return connectors.stream()
                .filter(connector -> connector.supports(sourceSystem))
                .findFirst()
                .orElseThrow(() -> new ApplicationException(HttpStatus.BAD_REQUEST, "same.legacy-patient.connector-missing",
                        "Conector não disponível para a fonte informada."));
    }

    private Tenant loadTenant(Long tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "tenant.not-found", "Tenant não encontrado."));
    }

    private SameLegacyPatientSnapshotDto toDto(SameLegacyPatientSnapshot snapshot) {
        return new SameLegacyPatientSnapshotDto(
                snapshot.getId(),
                snapshot.getTenant().getId(),
                snapshot.getSourceSystem(),
                snapshot.getExternalPatientId(),
                snapshot.getMedicalRecordCode(),
                snapshot.getFullName(),
                snapshot.getMotherName(),
                snapshot.getBirthDate(),
                snapshot.getCpf(),
                snapshot.getCns(),
                snapshot.getSex(),
                snapshot.getRawPayloadJson(),
                snapshot.getImportedAt()
        );
    }

    private String toJson(LegacyPatientRecord record) {
        try {
            return objectMapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String normalizeCode(String value) {
        String normalized = normalizeNullable(value);
        return normalized == null ? null : normalized.toUpperCase();
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String onlyDigits(String value) {
        if (value == null) {
            return null;
        }
        String digits = value.replaceAll("\\D", "");
        return digits.isBlank() ? null : digits;
    }
}
