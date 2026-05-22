package com.erp.qualitascareapi.same.application;

import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.same.api.dto.SameClinicalDocumentDto;
import com.erp.qualitascareapi.same.api.dto.SameClinicalDocumentMetadataRequest;
import com.erp.qualitascareapi.same.domain.SameClinicalDocument;
import com.erp.qualitascareapi.same.domain.SamePatientIdentifier;
import com.erp.qualitascareapi.same.domain.SamePatientMaster;
import com.erp.qualitascareapi.same.enums.SameAccessAction;
import com.erp.qualitascareapi.same.enums.SameDocumentStatus;
import com.erp.qualitascareapi.same.enums.SameDocumentType;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import com.erp.qualitascareapi.same.repo.SameClinicalDocumentRepository;
import com.erp.qualitascareapi.same.repo.SamePatientIdentifierRepository;
import com.erp.qualitascareapi.same.repo.SamePatientMasterRepository;
import com.erp.qualitascareapi.same.storage.SameFileStorageService;
import com.erp.qualitascareapi.same.storage.SameStoredFile;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.JoinType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Locale;

@Service
@Transactional
public class SameClinicalDocumentService {

    private final SameClinicalDocumentRepository documentRepository;
    private final SamePatientMasterRepository patientRepository;
    private final SamePatientIdentifierRepository identifierRepository;
    private final UserRepository userRepository;
    private final SameFileStorageService fileStorageService;
    private final SameDocumentAuditService auditService;
    private final TenantScopeGuard tenantScopeGuard;
    private final long maxFileSizeBytes;

    public SameClinicalDocumentService(SameClinicalDocumentRepository documentRepository,
                                       SamePatientMasterRepository patientRepository,
                                       SamePatientIdentifierRepository identifierRepository,
                                       UserRepository userRepository,
                                       SameFileStorageService fileStorageService,
                                       SameDocumentAuditService auditService,
                                       TenantScopeGuard tenantScopeGuard,
                                       @Value("${same.storage.max-file-size-bytes:52428800}") long maxFileSizeBytes) {
        this.documentRepository = documentRepository;
        this.patientRepository = patientRepository;
        this.identifierRepository = identifierRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.auditService = auditService;
        this.tenantScopeGuard = tenantScopeGuard;
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    public SameClinicalDocumentDto upload(Long patientMasterId,
                                          Long patientIdentifierId,
                                          SameDocumentType documentType,
                                          SameSourceSystem sourceSystem,
                                          String originalMedicalRecordCode,
                                          String attendanceCode,
                                          LocalDate attendanceDate,
                                          LocalDate documentPeriodStart,
                                          LocalDate documentPeriodEnd,
                                          String description,
                                          MultipartFile file,
                                          HttpServletRequest request) {
        SamePatientMaster patient = loadPatient(patientMasterId);
        SamePatientIdentifier identifier = loadIdentifier(patient, patientIdentifierId);
        validatePdf(file);

        SameStoredFile storedFile = fileStorageService.storePdf(file, patient.getTenant().getId(), patient.getId());
        SameClinicalDocument document = new SameClinicalDocument();
        document.setTenant(patient.getTenant());
        document.setPatientMaster(patient);
        document.setPatientIdentifier(identifier);
        document.setDocumentType(documentType);
        document.setSourceSystem(sourceSystem);
        document.setOriginalMedicalRecordCode(normalizeCode(originalMedicalRecordCode));
        document.setAttendanceCode(normalizeNullable(attendanceCode));
        document.setAttendanceDate(attendanceDate);
        document.setDocumentPeriodStart(documentPeriodStart);
        document.setDocumentPeriodEnd(documentPeriodEnd);
        document.setDescription(normalizeNullable(description));
        document.setFileName(originalFilename(file));
        document.setFilePath(storedFile.filePath());
        document.setFileHashSha256(storedFile.fileHashSha256());
        document.setMimeType(storedFile.mimeType());
        document.setFileSize(storedFile.fileSize());
        document.setLegalValue(false);
        document.setLegalValueNote(SameClinicalDocument.DEFAULT_LEGAL_VALUE_NOTE);
        loadCurrentUser().ifPresent(document::setCreatedBy);

        SameClinicalDocument saved = documentRepository.save(document);
        auditService.register(saved, SameAccessAction.UPLOAD, request);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<SameClinicalDocumentDto> search(Long patientMasterId,
                                                String patientName,
                                                String cpf,
                                                String medicalRecordCode,
                                                SameSourceSystem sourceSystem,
                                                SameDocumentType documentType,
                                                LocalDate periodStart,
                                                LocalDate periodEnd,
                                                String attendanceCode,
                                                SameDocumentStatus status,
                                                Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        Specification<SameClinicalDocument> specification = Specification
                .where(hasTenant(tenantId))
                .and(patientMasterId == null ? null : (root, query, cb) ->
                        cb.equal(root.get("patientMaster").get("id"), patientMasterId))
                .and(hasPatientName(patientName))
                .and(hasPatientCpf(cpf))
                .and(hasMedicalRecordCode(medicalRecordCode))
                .and(sourceSystem == null ? null : (root, query, cb) ->
                        cb.equal(root.get("sourceSystem"), sourceSystem))
                .and(documentType == null ? null : (root, query, cb) ->
                        cb.equal(root.get("documentType"), documentType))
                .and(status == null ? null : (root, query, cb) ->
                        cb.equal(root.get("status"), status))
                .and(hasAttendanceCode(attendanceCode))
                .and(overlapsPeriod(periodStart, periodEnd));
        return documentRepository.findAll(specification, pageable).map(this::toDto);
    }

    public SameClinicalDocumentDto findById(Long id, HttpServletRequest request) {
        SameClinicalDocument document = loadDocument(id);
        auditService.register(document, SameAccessAction.VIEW, request);
        return toDto(document);
    }

    public SameClinicalDocumentDto updateMetadata(Long id,
                                                  SameClinicalDocumentMetadataRequest metadata,
                                                  HttpServletRequest request) {
        SameClinicalDocument document = loadDocument(id);
        SamePatientIdentifier identifier = loadIdentifier(document.getPatientMaster(), metadata.patientIdentifierId());
        document.setPatientIdentifier(identifier);
        document.setDocumentType(metadata.documentType());
        document.setSourceSystem(metadata.sourceSystem());
        document.setOriginalMedicalRecordCode(normalizeCode(metadata.originalMedicalRecordCode()));
        document.setAttendanceCode(normalizeNullable(metadata.attendanceCode()));
        document.setAttendanceDate(metadata.attendanceDate());
        document.setDocumentPeriodStart(metadata.documentPeriodStart());
        document.setDocumentPeriodEnd(metadata.documentPeriodEnd());
        document.setDescription(normalizeNullable(metadata.description()));
        SameClinicalDocument saved = documentRepository.save(document);
        auditService.register(saved, SameAccessAction.UPDATE_METADATA, request);
        return toDto(saved);
    }

    public SameClinicalDocumentDto archive(Long id, HttpServletRequest request) {
        SameClinicalDocument document = loadDocument(id);
        document.setStatus(SameDocumentStatus.ARCHIVED);
        SameClinicalDocument saved = documentRepository.save(document);
        auditService.register(saved, SameAccessAction.ARCHIVE, request);
        return toDto(saved);
    }

    public SameClinicalDocumentDto block(Long id, HttpServletRequest request) {
        SameClinicalDocument document = loadDocument(id);
        document.setStatus(SameDocumentStatus.BLOCKED);
        SameClinicalDocument saved = documentRepository.save(document);
        auditService.register(saved, SameAccessAction.BLOCK, request);
        return toDto(saved);
    }

    public Resource download(Long id, HttpServletRequest request) {
        SameClinicalDocument document = loadDocument(id);
        if (document.getStatus() == SameDocumentStatus.BLOCKED) {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "same.document.blocked",
                    "Este documento está bloqueado e não pode ser baixado.");
        }
        Resource resource = fileStorageService.load(document.getFilePath());
        auditService.register(document, SameAccessAction.DOWNLOAD, request);
        return resource;
    }

    public SameClinicalDocument loadDocument(Long id) {
        return documentRepository.findByIdAndTenantId(id, tenantScopeGuard.currentTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Documento SAME não encontrado"));
    }

    private SamePatientMaster loadPatient(Long patientMasterId) {
        return patientRepository.findByIdAndTenantId(patientMasterId, tenantScopeGuard.currentTenantId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente SAME não encontrado"));
    }

    private SamePatientIdentifier loadIdentifier(SamePatientMaster patient, Long patientIdentifierId) {
        if (patientIdentifierId == null) {
            return null;
        }
        return identifierRepository
                .findByIdAndTenantIdAndPatientMasterId(patientIdentifierId, patient.getTenant().getId(), patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Identificador do paciente não encontrado"));
    }

    private java.util.Optional<User> loadCurrentUser() {
        Long userId = tenantScopeGuard.currentContext().userId();
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (userId == null || tenantId == null) {
            return java.util.Optional.empty();
        }
        return userRepository.findByIdAndTenant_Id(userId, tenantId);
    }

    private void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "same.document.file-empty",
                    "Informe um arquivo PDF para anexar ao prontuário.");
        }
        if (file.getSize() > maxFileSizeBytes) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "same.document.file-too-large",
                    "O PDF informado excede o tamanho máximo permitido.");
        }
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase(Locale.ROOT) : "";
        String contentType = file.getContentType() != null ? file.getContentType().toLowerCase(Locale.ROOT) : "";
        if (!filename.endsWith(".pdf") && !"application/pdf".equals(contentType)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "same.document.invalid-file-type",
                    "Envie apenas arquivos PDF.");
        }
    }

    private SameClinicalDocumentDto toDto(SameClinicalDocument d) {
        return new SameClinicalDocumentDto(
                d.getId(),
                d.getTenant().getId(),
                d.getPatientMaster().getId(),
                d.getPatientMaster().getFullName(),
                d.getPatientMaster().getCpf(),
                d.getPatientIdentifier() != null ? d.getPatientIdentifier().getId() : null,
                d.getDocumentType(),
                d.getSourceSystem(),
                d.getOriginalMedicalRecordCode(),
                d.getAttendanceCode(),
                d.getAttendanceDate(),
                d.getDocumentPeriodStart(),
                d.getDocumentPeriodEnd(),
                d.getFileName(),
                d.getFileHashSha256(),
                d.getMimeType(),
                d.getFileSize(),
                d.isLegalValue(),
                d.getLegalValueNote(),
                d.getDescription(),
                d.getStatus(),
                d.getCreatedBy() != null ? d.getCreatedBy().getId() : null,
                d.getCreatedBy() != null ? d.getCreatedBy().getUsername() : null,
                d.getCreatedAt(),
                d.getUpdatedAt()
        );
    }

    private Specification<SameClinicalDocument> hasTenant(Long tenantId) {
        return (root, query, cb) -> cb.equal(root.get("tenant").get("id"), tenantId);
    }

    private Specification<SameClinicalDocument> hasPatientName(String patientName) {
        if (patientName == null || patientName.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.like(
                cb.lower(root.join("patientMaster", JoinType.INNER).get("fullName")),
                "%" + patientName.trim().toLowerCase(Locale.ROOT) + "%");
    }

    private Specification<SameClinicalDocument> hasPatientCpf(String cpf) {
        String digits = onlyDigits(cpf);
        if (digits == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.join("patientMaster", JoinType.INNER).get("cpf"), digits);
    }

    private Specification<SameClinicalDocument> hasMedicalRecordCode(String medicalRecordCode) {
        String code = normalizeCode(medicalRecordCode);
        if (code == null) {
            return null;
        }
        return (root, query, cb) -> cb.or(
                cb.equal(root.get("originalMedicalRecordCode"), code),
                cb.equal(root.join("patientIdentifier", JoinType.LEFT).get("medicalRecordCode"), code)
        );
    }

    private Specification<SameClinicalDocument> hasAttendanceCode(String attendanceCode) {
        String code = normalizeNullable(attendanceCode);
        if (code == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("attendanceCode"), code);
    }

    private Specification<SameClinicalDocument> overlapsPeriod(LocalDate start, LocalDate end) {
        if (start == null && end == null) {
            return null;
        }
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.and(
                        cb.lessThanOrEqualTo(root.get("documentPeriodStart"), end),
                        cb.greaterThanOrEqualTo(root.get("documentPeriodEnd"), start)
                );
            }
            if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("documentPeriodEnd"), start);
            }
            return cb.lessThanOrEqualTo(root.get("documentPeriodStart"), end);
        };
    }

    private String originalFilename(MultipartFile file) {
        String original = file.getOriginalFilename();
        return original == null || original.isBlank() ? "prontuario.pdf" : original.trim();
    }

    private String normalizeCode(String value) {
        String normalized = normalizeNullable(value);
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
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
