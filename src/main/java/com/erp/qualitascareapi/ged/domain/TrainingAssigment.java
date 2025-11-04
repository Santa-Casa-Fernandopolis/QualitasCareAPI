package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.ged.enums.TrainingStatus;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Audited
@Table(name = "training_assignment",
        uniqueConstraints = @UniqueConstraint(name = "uq_training_docversion_user",
                columnNames = {"tenant_id","document_version_id","usuario_id"}))
public class TrainingAssignment {

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
    private TrainingStatus status;

    private LocalDate dataLimite;
    private LocalDateTime concluidoEm;

    private Integer nota;

    public TrainingAssignment() {}
    public TrainingAssignment(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public DocumentVersion getDocumentVersion() { return documentVersion; }
    public User getUsuario() { return usuario; }
    public TrainingStatus getStatus() { return status; }
    public LocalDate getDataLimite() { return dataLimite; }
    public LocalDateTime getConcluidoEm() { return concluidoEm; }
    public Integer getNota() { return nota; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setDocumentVersion(DocumentVersion documentVersion) { this.documentVersion = documentVersion; }
    public void setUsuario(User usuario) { this.usuario = usuario; }
    public void setStatus(TrainingStatus status) { this.status = status; }
    public void setDataLimite(LocalDate dataLimite) { this.dataLimite = dataLimite; }
    public void setConcluidoEm(LocalDateTime concluidoEm) { this.concluidoEm = concluidoEm; }
    public void setNota(Integer nota) { this.nota = nota; }
}
