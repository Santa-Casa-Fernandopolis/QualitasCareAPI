package com.erp.qualitascareapi.same.application;

import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.same.api.dto.*;
import com.erp.qualitascareapi.same.domain.SamePatientIdentifier;
import com.erp.qualitascareapi.same.domain.SamePatientMaster;
import com.erp.qualitascareapi.same.enums.SameConfidenceLevel;
import com.erp.qualitascareapi.same.enums.SamePatientStatus;
import com.erp.qualitascareapi.same.repo.SamePatientIdentifierRepository;
import com.erp.qualitascareapi.same.repo.SamePatientMasterRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class SamePatientService {

    private final SamePatientMasterRepository patientRepository;
    private final SamePatientIdentifierRepository identifierRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public SamePatientService(SamePatientMasterRepository patientRepository,
                              SamePatientIdentifierRepository identifierRepository,
                              TenantRepository tenantRepository,
                              TenantScopeGuard tenantScopeGuard) {
        this.patientRepository = patientRepository;
        this.identifierRepository = identifierRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public SamePatientDto create(SamePatientRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = loadTenant(request.tenantId());

        SamePatientMaster patient = new SamePatientMaster();
        patient.setTenant(tenant);
        applyFields(patient, request);
        patient.setStatus(SamePatientStatus.ACTIVE);

        return toDto(patientRepository.save(patient));
    }

    public SamePatientDto update(Long id, SamePatientRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        SamePatientMaster patient = loadPatient(id);
        if (!patient.getTenant().getId().equals(request.tenantId())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "same.patient.tenant-change",
                    "Não é permitido alterar o tenant do paciente.");
        }
        applyFields(patient, request);
        return toDto(patientRepository.save(patient));
    }

    public SamePatientDto updateStatus(Long id, SamePatientStatus status) {
        SamePatientMaster patient = loadPatient(id);
        patient.setStatus(status);
        return toDto(patientRepository.save(patient));
    }

    @Transactional(readOnly = true)
    public SamePatientDto findById(Long id) {
        return toDto(loadPatient(id));
    }

    @Transactional(readOnly = true)
    public Page<SamePatientDto> search(String query, Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        String normalizedQuery = normalizeSearch(query);
        Page<SamePatientMaster> page = patientRepository.search(tenantId, normalizedQuery, pageable);
        return page.map(this::toDto);
    }

    public SamePatientIdentifierDto addIdentifier(Long patientId, SamePatientIdentifierRequest request) {
        SamePatientMaster patient = loadPatient(patientId);
        String medicalRecordCode = normalizeCode(request.medicalRecordCode());
        if (identifierRepository.existsByTenantIdAndSourceSystemAndMedicalRecordCode(
                patient.getTenant().getId(), request.sourceSystem(), medicalRecordCode)) {
            throw new ApplicationException(HttpStatus.CONFLICT, "same.identifier.duplicate",
                    "Já existe um paciente vinculado a este sistema e código de prontuário.",
                    Map.of("sourceSystem", request.sourceSystem(), "medicalRecordCode", medicalRecordCode));
        }

        if (Boolean.TRUE.equals(request.primaryIdentifier())) {
            identifierRepository.findAllByTenantIdAndPatientMasterId(patient.getTenant().getId(), patient.getId())
                    .forEach(identifier -> identifier.setPrimaryIdentifier(false));
        }

        SamePatientIdentifier identifier = new SamePatientIdentifier();
        identifier.setTenant(patient.getTenant());
        identifier.setPatientMaster(patient);
        identifier.setSourceSystem(request.sourceSystem());
        identifier.setMedicalRecordCode(medicalRecordCode);
        identifier.setExternalPatientId(normalizeNullable(request.externalPatientId()));
        identifier.setPrimaryIdentifier(Boolean.TRUE.equals(request.primaryIdentifier()));
        identifier.setConfidenceLevel(request.confidenceLevel() != null ? request.confidenceLevel() : SameConfidenceLevel.HIGH);

        return toIdentifierDto(identifierRepository.save(identifier));
    }

    @Transactional(readOnly = true)
    public List<SamePatientIdentifierDto> listIdentifiers(Long patientId) {
        SamePatientMaster patient = loadPatient(patientId);
        return identifierRepository.findAllByTenantIdAndPatientMasterId(patient.getTenant().getId(), patient.getId())
                .stream()
                .map(this::toIdentifierDto)
                .toList();
    }

    public void deleteIdentifier(Long patientId, Long identifierId) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        SamePatientIdentifier identifier = identifierRepository
                .findByIdAndTenantIdAndPatientMasterId(identifierId, tenantId, patientId)
                .orElseThrow(() -> new EntityNotFoundException("Identificador do paciente não encontrado"));
        identifierRepository.delete(identifier);
    }

    private void applyFields(SamePatientMaster patient, SamePatientRequest request) {
        patient.setFullName(normalizeName(request.fullName()));
        patient.setMotherName(normalizeName(request.motherName()));
        patient.setBirthDate(request.birthDate());
        patient.setCpf(normalizeNullableDigits(request.cpf()));
        patient.setCns(normalizeNullableDigits(request.cns()));
        patient.setSex(request.sex());
    }

    private SamePatientMaster loadPatient(Long id) {
        return patientRepository.findByIdAndTenantId(id, tenantScopeGuard.currentTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente SAME não encontrado"));
    }

    private Tenant loadTenant(Long tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
    }

    private SamePatientDto toDto(SamePatientMaster patient) {
        List<SamePatientIdentifierDto> identifiers = identifierRepository
                .findAllByTenantIdAndPatientMasterId(patient.getTenant().getId(), patient.getId())
                .stream()
                .map(this::toIdentifierDto)
                .toList();
        return new SamePatientDto(
                patient.getId(),
                patient.getTenant().getId(),
                patient.getFullName(),
                patient.getMotherName(),
                patient.getBirthDate(),
                patient.getCpf(),
                patient.getCns(),
                patient.getSex(),
                patient.getStatus(),
                identifiers,
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }

    private SamePatientIdentifierDto toIdentifierDto(SamePatientIdentifier identifier) {
        return new SamePatientIdentifierDto(
                identifier.getId(),
                identifier.getPatientMaster().getId(),
                identifier.getSourceSystem(),
                identifier.getMedicalRecordCode(),
                identifier.getExternalPatientId(),
                identifier.isPrimaryIdentifier(),
                identifier.getConfidenceLevel(),
                identifier.getCreatedAt(),
                identifier.getUpdatedAt()
        );
    }

    private String normalizeSearch(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeCode(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeName(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim().replaceAll("\\s+", " ");
        return trimmed.isBlank() ? null : trimmed;
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String normalizeNullableDigits(String value) {
        if (value == null) {
            return null;
        }
        String digits = value.replaceAll("\\D", "");
        return digits.isBlank() ? null : digits;
    }
}
