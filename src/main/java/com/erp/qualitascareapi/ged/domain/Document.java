package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.ged.enums.ConfidentialityLevel;
import com.erp.qualitascareapi.ged.enums.DocumentStatus;
import com.erp.qualitascareapi.ged.enums.DocumentType;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
@Table(name = "documents",
        uniqueConstraints = @UniqueConstraint(name = "uq_documents_tenant_codigo",
                columnNames = {"tenant_id", "codigo"}))
public class Document {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @Column(nullable = false, length = 60)
    private String codigo; // ex.: CME-POP-0007

    @Column(nullable = false, length = 200)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DocumentType tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DocumentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConfidentialityLevel confidencialidade;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="categoria_id")
    private DocCategory categoria;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="setor_responsavel_id")
    private Setor setorResponsavel;

    private LocalDate dataVigenciaInicio;
    private LocalDate dataVigenciaFim;

    /** ponteiro para a versão publicada/atual */
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="versao_atual_id")
    private DocumentVersion versaoAtual;

    @Column(length = 30)
    private String nivelONATarget;

    @Column(length = 500)
    private String regulacoes;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="politica_retencao_id")
    private RetentionPolicy politicaRetencao;

    @Column(nullable = false)
    private Boolean exigeTreinamento = Boolean.FALSE;

    @ManyToMany
    @JoinTable(
            name = "document_tags",
            joinColumns = @JoinColumn(name = "document_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            uniqueConstraints = @UniqueConstraint(name = "uq_document_tags_doc_tag", columnNames = {"document_id","tag_id"})
    )
    private Set<DocTag> tags = new HashSet<>();

    public Document() {}
    public Document(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public String getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public DocumentType getTipo() { return tipo; }
    public DocumentStatus getStatus() { return status; }
    public ConfidentialityLevel getConfidencialidade() { return confidencialidade; }
    public DocCategory getCategoria() { return categoria; }
    public Setor getSetorResponsavel() { return setorResponsavel; }
    public LocalDate getDataVigenciaInicio() { return dataVigenciaInicio; }
    public LocalDate getDataVigenciaFim() { return dataVigenciaFim; }
    public DocumentVersion getVersaoAtual() { return versaoAtual; }
    public String getNivelONATarget() { return nivelONATarget; }
    public String getRegulacoes() { return regulacoes; }
    public RetentionPolicy getPoliticaRetencao() { return politicaRetencao; }
    public Boolean getExigeTreinamento() { return exigeTreinamento; }
    public Set<DocTag> getTags() { return tags; }

    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setTipo(DocumentType tipo) { this.tipo = tipo; }
    public void setStatus(DocumentStatus status) { this.status = status; }
    public void setConfidencialidade(ConfidentialityLevel confidencialidade) { this.confidencialidade = confidencialidade; }
    public void setCategoria(DocCategory categoria) { this.categoria = categoria; }
    public void setSetorResponsavel(Setor setorResponsavel) { this.setorResponsavel = setorResponsavel; }
    public void setDataVigenciaInicio(LocalDate dataVigenciaInicio) { this.dataVigenciaInicio = dataVigenciaInicio; }
    public void setDataVigenciaFim(LocalDate dataVigenciaFim) { this.dataVigenciaFim = dataVigenciaFim; }
    public void setVersaoAtual(DocumentVersion versaoAtual) { this.versaoAtual = versaoAtual; }
    public void setNivelONATarget(String nivelONATarget) { this.nivelONATarget = nivelONATarget; }
    public void setRegulacoes(String regulacoes) { this.regulacoes = regulacoes; }
    public void setPoliticaRetencao(RetentionPolicy politicaRetencao) { this.politicaRetencao = politicaRetencao; }
    public void setExigeTreinamento(Boolean exigeTreinamento) { this.exigeTreinamento = exigeTreinamento; }
    public void setTags(Set<DocTag> tags) { this.tags = tags; }
}
