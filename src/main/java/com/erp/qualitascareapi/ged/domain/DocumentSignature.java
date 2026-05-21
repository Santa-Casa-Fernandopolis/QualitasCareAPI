package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.ged.enums.DocumentSignatureStatus;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(name = "document_signatures",
        indexes = {
                @Index(name = "idx_doc_signature_version", columnList = "document_version_id,status"),
                @Index(name = "idx_doc_signature_signer", columnList = "signer_id,status")
        })
public class DocumentSignature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_version_id", nullable = false)
    private DocumentVersion documentVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "signer_id", nullable = false)
    private User signer;

    @Column(length = 120)
    private String roleLabel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DocumentSignatureStatus status = DocumentSignatureStatus.PENDENTE;

    private LocalDateTime requestedAt;
    private LocalDateTime signedAt;

    @Column(length = 500)
    private String comment;

    public DocumentSignature() {
    }

    @PrePersist
    public void prePersist() {
        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public DocumentVersion getDocumentVersion() { return documentVersion; }
    public void setDocumentVersion(DocumentVersion documentVersion) { this.documentVersion = documentVersion; }
    public User getSigner() { return signer; }
    public void setSigner(User signer) { this.signer = signer; }
    public String getRoleLabel() { return roleLabel; }
    public void setRoleLabel(String roleLabel) { this.roleLabel = roleLabel; }
    public DocumentSignatureStatus getStatus() { return status; }
    public void setStatus(DocumentSignatureStatus status) { this.status = status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public LocalDateTime getSignedAt() { return signedAt; }
    public void setSignedAt(LocalDateTime signedAt) { this.signedAt = signedAt; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
