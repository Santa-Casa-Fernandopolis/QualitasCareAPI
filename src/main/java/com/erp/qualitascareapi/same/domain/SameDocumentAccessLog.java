package com.erp.qualitascareapi.same.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.same.enums.SameAccessAction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "same_document_access_logs",
        indexes = {
                @Index(name = "idx_same_access_doc", columnList = "clinical_document_id,created_at"),
                @Index(name = "idx_same_access_patient", columnList = "patient_master_id,created_at"),
                @Index(name = "idx_same_access_tenant_user", columnList = "tenant_id,user_id,created_at")
        })
public class SameDocumentAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinical_document_id", nullable = false)
    private SameClinicalDocument clinicalDocument;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_master_id", nullable = false)
    private SamePatientMaster patientMaster;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name", length = 160)
    private String userName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SameAccessAction action;

    @Column(name = "ip_address", length = 80)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public SameClinicalDocument getClinicalDocument() { return clinicalDocument; }
    public void setClinicalDocument(SameClinicalDocument clinicalDocument) { this.clinicalDocument = clinicalDocument; }
    public SamePatientMaster getPatientMaster() { return patientMaster; }
    public void setPatientMaster(SamePatientMaster patientMaster) { this.patientMaster = patientMaster; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public SameAccessAction getAction() { return action; }
    public void setAction(SameAccessAction action) { this.action = action; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
