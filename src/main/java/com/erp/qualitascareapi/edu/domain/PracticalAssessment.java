package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.hr.domain.Colaborador;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(name="edu_practical_assessments")
public class PracticalAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="rubric_id", nullable=false)
    private CompetencyRubric rubric;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="colaborador_id", nullable=false)
    private Colaborador colaborador;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="avaliador_id", nullable=false)
    private Colaborador avaliador;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="setor_id", nullable=false)
    private Setor realizadoNoSetor;

    private LocalDateTime realizadoEm;

    private Double notaFinal;

    @Column(nullable=false)
    private Boolean aprovado;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="evidencia_id")
    private EvidenciaArquivo evidencia;

    @Column(columnDefinition = "text")
    private String observacoes;

    public PracticalAssessment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public CompetencyRubric getRubric() { return rubric; }
    public void setRubric(CompetencyRubric rubric) { this.rubric = rubric; }
    public Colaborador getColaborador() { return colaborador; }
    public void setColaborador(Colaborador colaborador) { this.colaborador = colaborador; }
    public Colaborador getAvaliador() { return avaliador; }
    public void setAvaliador(Colaborador avaliador) { this.avaliador = avaliador; }
    public Setor getRealizadoNoSetor() { return realizadoNoSetor; }
    public void setRealizadoNoSetor(Setor realizadoNoSetor) { this.realizadoNoSetor = realizadoNoSetor; }
    public LocalDateTime getRealizadoEm() { return realizadoEm; }
    public void setRealizadoEm(LocalDateTime realizadoEm) { this.realizadoEm = realizadoEm; }
    public Double getNotaFinal() { return notaFinal; }
    public void setNotaFinal(Double notaFinal) { this.notaFinal = notaFinal; }
    public Boolean getAprovado() { return aprovado; }
    public void setAprovado(Boolean aprovado) { this.aprovado = aprovado; }
    public EvidenciaArquivo getEvidencia() { return evidencia; }
    public void setEvidencia(EvidenciaArquivo evidencia) { this.evidencia = evidencia; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}

