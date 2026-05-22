package com.erp.qualitascareapi.same.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.same.enums.SamePatientStatus;
import com.erp.qualitascareapi.same.enums.SameSex;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Audited
@Entity
@Table(name = "same_patient_master",
        indexes = {
                @Index(name = "idx_same_patient_tenant_name", columnList = "tenant_id,full_name"),
                @Index(name = "idx_same_patient_tenant_cpf", columnList = "tenant_id,cpf"),
                @Index(name = "idx_same_patient_tenant_cns", columnList = "tenant_id,cns"),
                @Index(name = "idx_same_patient_tenant_birth", columnList = "tenant_id,birth_date")
        })
public class SamePatientMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotBlank
    @Column(name = "full_name", nullable = false, length = 180)
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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SamePatientStatus status = SamePatientStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = SamePatientStatus.ACTIVE;
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
    public SamePatientStatus getStatus() { return status; }
    public void setStatus(SamePatientStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
