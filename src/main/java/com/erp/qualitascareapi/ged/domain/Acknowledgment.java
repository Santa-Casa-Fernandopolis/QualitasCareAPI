package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.ged.enums.AckStatus;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Audited
@Table(name = "acknowledgment",
        uniqueConstraints = @UniqueConstraint(name = "uq_ack_docversion_user",
                columnNames = {"tenant_id","document_version_id","usuario_id"}))
public class Acknowledgment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="document_version_id", nullable=false)
    private DocumentVersion documentVersion;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="usuario_id", nullable=false)
    private User usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=15)
    private AckStatus status;

    private LocalDate dataLimite;
    private LocalDateTime cienteEm;

    public Acknowledgment() {}
    public Acknowledgment(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public DocumentVersion getDocumentVersion() { return documentVersion; }
    public User getUsuario() { return usuario; }
    public AckStatus getStatus() { return status; }
    public LocalDate getDataLimite() { return dataLimite; }
    public LocalDateTime getCienteEm() { return cienteEm; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setDocumentVersion(DocumentVersion documentVersion) { this.documentVersion = documentVersion; }
    public void setUsuario(User usuario) { this.usuario = usuario; }
    public void setStatus(AckStatus status) { this.status = status; }
    public void setDataLimite(LocalDate dataLimite) { this.dataLimite = dataLimite; }
    public void setCienteEm(LocalDateTime cienteEm) { this.cienteEm = cienteEm; }
}
