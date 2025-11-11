package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.approval.core.contracts.ApprovableTarget;
import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.edu.enums.TrainingPlanStatus;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Table(name = "edu_training_plan",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_train_plan_tenant_ano_setor",
                        columnNames = {"tenant_id","ano","setor_id"})
        },
        indexes = {
                @Index(name = "idx_train_plan_tenant_status", columnList = "tenant_id,status"),
                @Index(name = "idx_train_plan_setor", columnList = "setor_id")
        })
public class TrainingPlan implements ApprovableTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tenant_id", nullable = false)
    @NotNull
    private Tenant tenant;

    @Column(name = "ano", nullable = false)
    @Min(2000)
    private Integer ano;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "setor_id", nullable = false)
    @NotNull
    private Setor setor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 40, nullable = false)
    private TrainingPlanStatus status = TrainingPlanStatus.RASCUNHO;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "solicitante_id", nullable = false)
    private User solicitante;

    @Column(name = "enviado_em")
    private LocalDateTime enviadoEm;

    @Column(name = "aprovado_em")
    private LocalDateTime aprovadoEm;

    @Column(name = "justificativa", length = 1000)
    private String justificativa;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainingPlanItem> itens = new ArrayList<>();

    public TrainingPlan() {}
    public TrainingPlan(Long id) { this.id = id; }

    /* ------------ ApprovableTarget ------------ */
    @Override
    public Tenant getTenant() { return this.tenant; }

    @Override
    public ApprovalDomain getApprovalDomain() { return ApprovalDomain.PLANO_TREINAMENTO; }

    @Override
    public String getApprovalKey() { return "trainPlan:" + this.id; }

    @Override
    public Setor getScopeSetor() { return this.setor; }

    /* ------------ Getters/Setters ------------ */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenantField() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public Setor getSetor() { return setor; }
    public void setSetor(Setor setor) { this.setor = setor; }

    public TrainingPlanStatus getStatus() { return status; }
    public void setStatus(TrainingPlanStatus status) { this.status = status; }

    public User getSolicitante() { return solicitante; }
    public void setSolicitante(User solicitante) { this.solicitante = solicitante; }

    public LocalDateTime getEnviadoEm() { return enviadoEm; }
    public void setEnviadoEm(LocalDateTime enviadoEm) { this.enviadoEm = enviadoEm; }

    public LocalDateTime getAprovadoEm() { return aprovadoEm; }
    public void setAprovadoEm(LocalDateTime aprovadoEm) { this.aprovadoEm = aprovadoEm; }

    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }

    public List<TrainingPlanItem> getItens() { return itens; }
    public void setItens(List<TrainingPlanItem> itens) { this.itens = itens; }
}
