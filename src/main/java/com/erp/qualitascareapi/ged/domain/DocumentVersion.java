package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.ged.enums.DocumentStatus;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Audited
@Table(name = "document_version",
        uniqueConstraints = @UniqueConstraint(name = "uq_doc_version_scope",
                columnNames = {"tenant_id","documento_id","versao_major","versao_minor"}))
public class DocumentVersion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="documento_id", nullable=false)
    private Document documento;

    @Column(name="versao_major", nullable=false)
    private Integer versaoMajor;

    @Column(name="versao_minor", nullable=false)
    private Integer versaoMinor;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private DocumentStatus status;

    @Column(length=800)
    private String resumoMudancas;

    private LocalDate dataVigenciaInicio;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="pdf_arquivo_id")
    private EvidenciaArquivo pdfArquivo;

    @Column(length=64)
    private String pdfSha256;

    private LocalDateTime geradoEm;

    public DocumentVersion() {}
    public DocumentVersion(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public Document getDocumento() { return documento; }
    public Integer getVersaoMajor() { return versaoMajor; }
    public Integer getVersaoMinor() { return versaoMinor; }
    public DocumentStatus getStatus() { return status; }
    public String getResumoMudancas() { return resumoMudancas; }
    public LocalDate getDataVigenciaInicio() { return dataVigenciaInicio; }
    public EvidenciaArquivo getPdfArquivo() { return pdfArquivo; }
    public String getPdfSha256() { return pdfSha256; }
    public LocalDateTime getGeradoEm() { return geradoEm; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setDocumento(Document documento) { this.documento = documento; }
    public void setVersaoMajor(Integer versaoMajor) { this.versaoMajor = versaoMajor; }
    public void setVersaoMinor(Integer versaoMinor) { this.versaoMinor = versaoMinor; }
    public void setStatus(DocumentStatus status) { this.status = status; }
    public void setResumoMudancas(String resumoMudancas) { this.resumoMudancas = resumoMudancas; }
    public void setDataVigenciaInicio(LocalDate dataVigenciaInicio) { this.dataVigenciaInicio = dataVigenciaInicio; }
    public void setPdfArquivo(EvidenciaArquivo pdfArquivo) { this.pdfArquivo = pdfArquivo; }
    public void setPdfSha256(String pdfSha256) { this.pdfSha256 = pdfSha256; }
    public void setGeradoEm(LocalDateTime geradoEm) { this.geradoEm = geradoEm; }
}
