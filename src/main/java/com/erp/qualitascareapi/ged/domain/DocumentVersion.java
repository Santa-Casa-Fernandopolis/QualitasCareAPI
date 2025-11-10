package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.approval.core.contracts.ApprovableTarget;
import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.core.domain.Setor;
import com.erp.qualitascareapi.ged.enums.DocumentStatus;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Audited
@Table(
        name = "document_version",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_doc_version_scope",
                columnNames = {"tenant_id","documento_id","versao_major","versao_minor"}
        ),
        indexes = {
                @Index(name = "idx_docver_tenant_status", columnList = "tenant_id,status"),
                @Index(name = "idx_docver_tenant_doc_status", columnList = "tenant_id,documento_id,status"),
                @Index(name = "idx_docver_pdfsha", columnList = "pdf_sha256")
        }
)
public class DocumentVersion implements ApprovableTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Mantemos tenant local para filtros rápidos e multitenancy estável */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @NotNull
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_id", nullable = false)
    @NotNull
    private Document documento;

    @Column(name = "versao_major", nullable = false)
    @Min(0)
    private Integer versaoMajor;

    @Column(name = "versao_minor", nullable = false)
    @Min(0)
    private Integer versaoMinor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull
    private DocumentStatus status;

    @Column(length = 800)
    private String resumoMudancas;

    private LocalDate dataVigenciaInicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdf_arquivo_id")
    private EvidenciaArquivo pdfArquivo;

    @Size(max = 64)
    @Column(name = "pdf_sha256", length = 64)
    private String pdfSha256;

    private LocalDateTime geradoEm;

    public DocumentVersion() {}
    public DocumentVersion(Long id) { this.id = id; }

    /* ---------- Callbacks para consistência ---------- */

    @PrePersist
    @PreUpdate
    private void syncTenantFromDocumento() {
        if (this.documento != null && this.tenant == null) {
            this.tenant = this.documento.getTenant();
        }
        if (this.geradoEm == null) {
            this.geradoEm = LocalDateTime.now();
        }
    }

    /* ---------- ApprovableTarget ---------- */

    @Override
    public Tenant getTenant() {
        return this.tenant; // usa o campo local (estável)
    }

    @Override
    public ApprovalDomain getApprovalDomain() {
        return ApprovalDomain.DOCUMENT_VERSION;
    }

    @Override
    public String getApprovalKey() {
        return "docVersion:" + this.id;
    }

    @Override
    public Setor getScopeSetor() {
        return (this.documento != null) ? this.documento.getSetorResponsavel() : null;
    }

    /* ---------- Getters/Setters ---------- */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenantField() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Document getDocumento() { return documento; }
    public void setDocumento(Document documento) { this.documento = documento; }

    public Integer getVersaoMajor() { return versaoMajor; }
    public void setVersaoMajor(Integer versaoMajor) { this.versaoMajor = versaoMajor; }

    public Integer getVersaoMinor() { return versaoMinor; }
    public void setVersaoMinor(Integer versaoMinor) { this.versaoMinor = versaoMinor; }

    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }

    public String getResumoMudancas() { return resumoMudancas; }
    public void setResumoMudancas(String resumoMudancas) { this.resumoMudancas = resumoMudancas; }

    public LocalDate getDataVigenciaInicio() { return dataVigenciaInicio; }
    public void setDataVigenciaInicio(LocalDate dataVigenciaInicio) { this.dataVigenciaInicio = dataVigenciaInicio; }

    public EvidenciaArquivo getPdfArquivo() { return pdfArquivo; }
    public void setPdfArquivo(EvidenciaArquivo pdfArquivo) { this.pdfArquivo = pdfArquivo; }

    public String getPdfSha256() { return pdfSha256; }
    public void setPdfSha256(String pdfSha256) { this.pdfSha256 = pdfSha256; }

    public LocalDateTime getGeradoEm() { return geradoEm; }
    public void setGeradoEm(LocalDateTime geradoEm) { this.geradoEm = geradoEm; }

    /* ---------- Helpers opcionais ---------- */

    @Transient
    public String getSemVer() {
        return (versaoMajor != null && versaoMinor != null)
                ? versaoMajor + "." + versaoMinor
                : null;
    }
}
