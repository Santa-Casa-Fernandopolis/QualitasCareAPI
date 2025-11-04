package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "approval_stage",
        indexes = @Index(name = "idx_approval_stage_order", columnList = "document_version_id, ordem"))
public class ApprovalStage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="document_version_id", nullable=false)
    private DocumentVersion documentVersion;

    @Column(nullable=false)
    private Integer ordem;

    @Column(length=80)
    private String papelRequerido;

    @Column(length=80)
    private String departamentoRequerido;

    public ApprovalStage() {}
    public ApprovalStage(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public DocumentVersion getDocumentVersion() { return documentVersion; }
    public Integer getOrdem() { return ordem; }
    public String getPapelRequerido() { return papelRequerido; }
    public String getDepartamentoRequerido() { return departamentoRequerido; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setDocumentVersion(DocumentVersion documentVersion) { this.documentVersion = documentVersion; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }
    public void setPapelRequerido(String papelRequerido) { this.papelRequerido = papelRequerido; }
    public void setDepartamentoRequerido(String departamentoRequerido) { this.departamentoRequerido = departamentoRequerido; }
}
