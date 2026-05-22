package com.erp.qualitascareapi.same.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.same.enums.SameConfidenceLevel;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Audited
@Entity
@Table(name = "same_patient_identifiers",
        uniqueConstraints = @UniqueConstraint(name = "uq_same_identifier_tenant_source_code",
                columnNames = {"tenant_id", "source_system", "medical_record_code"}),
        indexes = {
                @Index(name = "idx_same_identifier_patient", columnList = "patient_master_id"),
                @Index(name = "idx_same_identifier_external", columnList = "tenant_id,external_patient_id")
        })
public class SamePatientIdentifier {

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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "source_system", nullable = false, length = 30)
    private SameSourceSystem sourceSystem;

    @NotBlank
    @Column(name = "medical_record_code", nullable = false, length = 80)
    private String medicalRecordCode;

    @Column(name = "external_patient_id", length = 80)
    private String externalPatientId;

    @Column(name = "is_primary", nullable = false)
    private boolean primaryIdentifier = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "confidence_level", nullable = false, length = 20)
    private SameConfidenceLevel confidenceLevel = SameConfidenceLevel.HIGH;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (confidenceLevel == null) {
            confidenceLevel = SameConfidenceLevel.HIGH;
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
    public SameSourceSystem getSourceSystem() { return sourceSystem; }
    public void setSourceSystem(SameSourceSystem sourceSystem) { this.sourceSystem = sourceSystem; }
    public String getMedicalRecordCode() { return medicalRecordCode; }
    public void setMedicalRecordCode(String medicalRecordCode) { this.medicalRecordCode = medicalRecordCode; }
    public String getExternalPatientId() { return externalPatientId; }
    public void setExternalPatientId(String externalPatientId) { this.externalPatientId = externalPatientId; }
    public boolean isPrimaryIdentifier() { return primaryIdentifier; }
    public void setPrimaryIdentifier(boolean primaryIdentifier) { this.primaryIdentifier = primaryIdentifier; }
    public SameConfidenceLevel getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(SameConfidenceLevel confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
