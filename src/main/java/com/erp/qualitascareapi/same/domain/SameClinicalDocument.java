package com.erp.qualitascareapi.same.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.same.enums.SameDocumentStatus;
import com.erp.qualitascareapi.same.enums.SameDocumentType;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Audited
@Entity
@Table(name = "same_clinical_documents",
        indexes = {
                @Index(name = "idx_same_doc_tenant_patient", columnList = "tenant_id,patient_master_id"),
                @Index(name = "idx_same_doc_tenant_record", columnList = "tenant_id,original_medical_record_code"),
                @Index(name = "idx_same_doc_tenant_source", columnList = "tenant_id,source_system"),
                @Index(name = "idx_same_doc_tenant_status", columnList = "tenant_id,status"),
                @Index(name = "idx_same_doc_hash", columnList = "file_hash_sha256")
        })
public class SameClinicalDocument {

    public static final String DEFAULT_LEGAL_VALUE_NOTE =
            "Cópia digitalizada para consulta administrativa e assistencial, sem valor legal definido.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_master_id", nullable = false)
    private SamePatientMaster patientMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_identifier_id")
    private SamePatientIdentifier patientIdentifier;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 40)
    private SameDocumentType documentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "source_system", nullable = false, length = 30)
    private SameSourceSystem sourceSystem;

    @Column(name = "original_medical_record_code", length = 80)
    private String originalMedicalRecordCode;

    @Column(name = "attendance_code", length = 80)
    private String attendanceCode;

    @Column(name = "attendance_date")
    private LocalDate attendanceDate;

    @Column(name = "document_period_start")
    private LocalDate documentPeriodStart;

    @Column(name = "document_period_end")
    private LocalDate documentPeriodEnd;

    @NotBlank
    @Column(name = "file_name", nullable = false, length = 180)
    private String fileName;

    @NotBlank
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @NotBlank
    @Column(name = "file_hash_sha256", nullable = false, length = 64)
    private String fileHashSha256;

    @NotBlank
    @Column(name = "mime_type", nullable = false, length = 120)
    private String mimeType;

    @NotNull
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "legal_value", nullable = false)
    private boolean legalValue = false;

    @Column(name = "legal_value_note", nullable = false, length = 300)
    private String legalValueNote = DEFAULT_LEGAL_VALUE_NOTE;

    @Column(length = 1000)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SameDocumentStatus status = SameDocumentStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (legalValueNote == null || legalValueNote.isBlank()) {
            legalValueNote = DEFAULT_LEGAL_VALUE_NOTE;
        }
        if (status == null) {
            status = SameDocumentStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public SamePatientMaster getPatientMaster() { return patientMaster; }
    public void setPatientMaster(SamePatientMaster patientMaster) { this.patientMaster = patientMaster; }
    public SamePatientIdentifier getPatientIdentifier() { return patientIdentifier; }
    public void setPatientIdentifier(SamePatientIdentifier patientIdentifier) { this.patientIdentifier = patientIdentifier; }
    public SameDocumentType getDocumentType() { return documentType; }
    public void setDocumentType(SameDocumentType documentType) { this.documentType = documentType; }
    public SameSourceSystem getSourceSystem() { return sourceSystem; }
    public void setSourceSystem(SameSourceSystem sourceSystem) { this.sourceSystem = sourceSystem; }
    public String getOriginalMedicalRecordCode() { return originalMedicalRecordCode; }
    public void setOriginalMedicalRecordCode(String originalMedicalRecordCode) { this.originalMedicalRecordCode = originalMedicalRecordCode; }
    public String getAttendanceCode() { return attendanceCode; }
    public void setAttendanceCode(String attendanceCode) { this.attendanceCode = attendanceCode; }
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
    public LocalDate getDocumentPeriodStart() { return documentPeriodStart; }
    public void setDocumentPeriodStart(LocalDate documentPeriodStart) { this.documentPeriodStart = documentPeriodStart; }
    public LocalDate getDocumentPeriodEnd() { return documentPeriodEnd; }
    public void setDocumentPeriodEnd(LocalDate documentPeriodEnd) { this.documentPeriodEnd = documentPeriodEnd; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getFileHashSha256() { return fileHashSha256; }
    public void setFileHashSha256(String fileHashSha256) { this.fileHashSha256 = fileHashSha256; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public boolean isLegalValue() { return legalValue; }
    public void setLegalValue(boolean legalValue) { this.legalValue = legalValue; }
    public String getLegalValueNote() { return legalValueNote; }
    public void setLegalValueNote(String legalValueNote) { this.legalValueNote = legalValueNote; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public SameDocumentStatus getStatus() { return status; }
    public void setStatus(SameDocumentStatus status) { this.status = status; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
