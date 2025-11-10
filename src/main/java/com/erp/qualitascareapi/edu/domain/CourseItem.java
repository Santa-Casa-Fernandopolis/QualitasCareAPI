package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.ged.domain.DocumentVersion;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name="edu_course_items")
public class CourseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="module_id", nullable=false)
    private CourseModule module;

    private Integer ordem;

    @Column(nullable=false, length=160)
    private String titulo;

    @Column(columnDefinition = "text")
    private String descricao;

    private Double aprovacaoMin;

    private Double frequenciaMinPct;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="documento_base_version_id")
    private DocumentVersion documentoBase;

    public CourseItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public CourseModule getModule() { return module; }
    public void setModule(CourseModule module) { this.module = module; }
    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Double getAprovacaoMin() { return aprovacaoMin; }
    public void setAprovacaoMin(Double aprovacaoMin) { this.aprovacaoMin = aprovacaoMin; }
    public Double getFrequenciaMinPct() { return frequenciaMinPct; }
    public void setFrequenciaMinPct(Double frequenciaMinPct) { this.frequenciaMinPct = frequenciaMinPct; }
    public DocumentVersion getDocumentoBase() { return documentoBase; }
    public void setDocumentoBase(DocumentVersion documentoBase) { this.documentoBase = documentoBase; }
}
