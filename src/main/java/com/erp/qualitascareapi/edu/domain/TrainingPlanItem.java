package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.edu.enums.DeliveryMode;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "edu_training_plan_item",
        indexes = {
                @Index(name = "idx_plan_item_tenant", columnList = "tenant_id"),
                @Index(name = "idx_plan_item_plan", columnList = "plan_id"),
                @Index(name = "idx_plan_item_course", columnList = "course_id")
        })
public class TrainingPlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tenant_id", nullable = false)
    @NotNull
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "plan_id", nullable = false)
    @NotNull
    private TrainingPlan plan;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "course_id", nullable = false)
    @NotNull
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_mode", length = 20, nullable = false)
    private DeliveryMode deliveryMode = DeliveryMode.PRESENCIAL;

    @Column(name = "carga_horaria_prevista")
    private Integer cargaHorariaPrevista;

    @Column(name = "mes_planejado")
    @Min(1) @Max(12)
    private Integer mesPlanejado;

    @Column(name = "vagas_previstas")
    @Min(0)
    private Integer vagasPrevistas;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "publico_alvo_setor_id")
    private Setor publicoAlvoSetor;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    public TrainingPlanItem() {}
    public TrainingPlanItem(Long id) { this.id = id; }

    /* ------------ Getters/Setters ------------ */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public TrainingPlan getPlan() { return plan; }
    public void setPlan(TrainingPlan plan) { this.plan = plan; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public DeliveryMode getDeliveryMode() { return deliveryMode; }
    public void setDeliveryMode(DeliveryMode deliveryMode) { this.deliveryMode = deliveryMode; }

    public Integer getCargaHorariaPrevista() { return cargaHorariaPrevista; }
    public void setCargaHorariaPrevista(Integer cargaHorariaPrevista) { this.cargaHorariaPrevista = cargaHorariaPrevista; }

    public Integer getMesPlanejado() { return mesPlanejado; }
    public void setMesPlanejado(Integer mesPlanejado) { this.mesPlanejado = mesPlanejado; }

    public Integer getVagasPrevistas() { return vagasPrevistas; }
    public void setVagasPrevistas(Integer vagasPrevistas) { this.vagasPrevistas = vagasPrevistas; }

    public Setor getPublicoAlvoSetor() { return publicoAlvoSetor; }
    public void setPublicoAlvoSetor(Setor publicoAlvoSetor) { this.publicoAlvoSetor = publicoAlvoSetor; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
