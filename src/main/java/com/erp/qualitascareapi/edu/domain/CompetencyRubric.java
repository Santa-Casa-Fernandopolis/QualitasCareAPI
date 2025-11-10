package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name="edu_competency_rubrics")
public class CompetencyRubric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="competency_id", nullable=false)
    private Competency competency;

    @Column(nullable=false, length=160)
    private String titulo;

    @Column(columnDefinition = "text")
    private String descricao;

    @Column(length=200)
    private String escalaDescricao;

    public CompetencyRubric() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public Competency getCompetency() { return competency; }
    public void setCompetency(Competency competency) { this.competency = competency; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getEscalaDescricao() { return escalaDescricao; }
    public void setEscalaDescricao(String escalaDescricao) { this.escalaDescricao = escalaDescricao; }
}

