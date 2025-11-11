package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.edu.enums.DeliveryMode;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Entity
@Audited
@Table(name="edu_offerings",
        uniqueConstraints=@UniqueConstraint(name="uq_edu_offering_codigo_tenant", columnNames={"tenant_id","codigo_turma"}))
public class Offering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="course_id", nullable=false)
    private Course course;

    @Column(name="codigo_turma", nullable=false, length=60)
    private String codigoTurma;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="setor_alvo_id")
    private Setor setorAlvo;

    private LocalDate inicio;
    private LocalDate fim;

    private Integer vagas;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private DeliveryMode deliveryMode;

    @Column(nullable=false)
    private Boolean ativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_item_id")
    private TrainingPlanItem planItem;

    public Offering() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public String getCodigoTurma() { return codigoTurma; }
    public void setCodigoTurma(String codigoTurma) { this.codigoTurma = codigoTurma; }
    public Setor getSetorAlvo() { return setorAlvo; }
    public void setSetorAlvo(Setor setorAlvo) { this.setorAlvo = setorAlvo; }
    public LocalDate getInicio() { return inicio; }
    public void setInicio(LocalDate inicio) { this.inicio = inicio; }
    public LocalDate getFim() { return fim; }
    public void setFim(LocalDate fim) { this.fim = fim; }
    public Integer getVagas() { return vagas; }
    public void setVagas(Integer vagas) { this.vagas = vagas; }
    public DeliveryMode getDeliveryMode() { return deliveryMode; }
    public void setDeliveryMode(DeliveryMode deliveryMode) { this.deliveryMode = deliveryMode; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public TrainingPlanItem getPlanItem() { return planItem; }
    public void setPlanItem(TrainingPlanItem planItem) { this.planItem = planItem; }
}
