package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.hr.domain.Colaborador;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name="edu_course_instructors")
public class CourseInstructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="provider_id", nullable=false)
    private TrainingProvider provider;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="colaborador_id", nullable=false)
    private Colaborador colaborador;

    @Column(columnDefinition = "text")
    private String curriculo;

    @Column(length=160)
    private String areaEspecialidade;

    @Column(nullable=false)
    private Boolean ativo;

    public CourseInstructor() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public TrainingProvider getProvider() { return provider; }
    public void setProvider(TrainingProvider provider) { this.provider = provider; }
    public Colaborador getColaborador() { return colaborador; }
    public void setColaborador(Colaborador colaborador) { this.colaborador = colaborador; }
    public String getCurriculo() { return curriculo; }
    public void setCurriculo(String curriculo) { this.curriculo = curriculo; }
    public String getAreaEspecialidade() { return areaEspecialidade; }
    public void setAreaEspecialidade(String areaEspecialidade) { this.areaEspecialidade = areaEspecialidade; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
