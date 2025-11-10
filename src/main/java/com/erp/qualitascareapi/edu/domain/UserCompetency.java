package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.hr.domain.Colaborador;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Entity
@Audited
@Table(name="edu_user_competencies")
public class UserCompetency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="colaborador_id", nullable=false)
    private Colaborador colaborador;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="competency_id", nullable=false)
    private Competency competency;

    private LocalDate obtidaEm;

    @Column(length=120)
    private String origem;

    private LocalDate validadeAte;

    public UserCompetency() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public Colaborador getColaborador() { return colaborador; }
    public void setColaborador(Colaborador colaborador) { this.colaborador = colaborador; }
    public Competency getCompetency() { return competency; }
    public void setCompetency(Competency competency) { this.competency = competency; }
    public LocalDate getObtidaEm() { return obtidaEm; }
    public void setObtidaEm(LocalDate obtidaEm) { this.obtidaEm = obtidaEm; }
    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }
    public LocalDate getValidadeAte() { return validadeAte; }
    public void setValidadeAte(LocalDate validadeAte) { this.validadeAte = validadeAte; }
}
