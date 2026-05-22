package com.erp.qualitascareapi.same.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.same.enums.SameSex;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "same_legacy_patient_snapshots",
        indexes = {
                @Index(name = "idx_same_legacy_snapshot_record", columnList = "tenant_id,source_system,medical_record_code"),
                @Index(name = "idx_same_legacy_snapshot_external", columnList = "tenant_id,source_system,external_patient_id"),
                @Index(name = "idx_same_legacy_snapshot_cpf", columnList = "tenant_id,cpf")
        })
public class SameLegacyPatientSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "source_system", nullable = false, length = 30)
    private SameSourceSystem sourceSystem;

    @Column(name = "external_patient_id", length = 80)
    private String externalPatientId;

    @Column(name = "medical_record_code", length = 80)
    private String medicalRecordCode;

    @Column(name = "full_name", length = 180)
    private String fullName;

    @Column(name = "mother_name", length = 180)
    private String motherName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 14)
    private String cpf;

    @Column(length = 20)
    private String cns;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SameSex sex;

    @Lob
    @Column(name = "raw_payload_json")
    private String rawPayloadJson;

    @Column(name = "imported_at", nullable = false, updatable = false)
    private LocalDateTime importedAt;

    @PrePersist
    public void prePersist() {
        if (importedAt == null) {
            importedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public SameSourceSystem getSourceSystem() { return sourceSystem; }
    public void setSourceSystem(SameSourceSystem sourceSystem) { this.sourceSystem = sourceSystem; }
    public String getExternalPatientId() { return externalPatientId; }
    public void setExternalPatientId(String externalPatientId) { this.externalPatientId = externalPatientId; }
    public String getMedicalRecordCode() { return medicalRecordCode; }
    public void setMedicalRecordCode(String medicalRecordCode) { this.medicalRecordCode = medicalRecordCode; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getMotherName() { return motherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getCns() { return cns; }
    public void setCns(String cns) { this.cns = cns; }
    public SameSex getSex() { return sex; }
    public void setSex(SameSex sex) { this.sex = sex; }
    public String getRawPayloadJson() { return rawPayloadJson; }
    public void setRawPayloadJson(String rawPayloadJson) { this.rawPayloadJson = rawPayloadJson; }
    public LocalDateTime getImportedAt() { return importedAt; }
    public void setImportedAt(LocalDateTime importedAt) { this.importedAt = importedAt; }
}
