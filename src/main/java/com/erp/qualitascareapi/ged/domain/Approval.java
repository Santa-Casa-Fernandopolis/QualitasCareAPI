package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.ged.enums.ApprovalDecision;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(name = "approval")
public class Approval {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="stage_id", nullable=false)
    private ApprovalStage stage;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="aprovador_id", nullable=false)
    private User aprovador;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private ApprovalDecision decisao;

    private LocalDateTime decididoEm;

    @Column(length=500)
    private String comentario;

    @Column(length=120)
    private String assinaturaEletronica;

    public Approval() {}
    public Approval(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public ApprovalStage getStage() { return stage; }
    public User getAprovador() { return aprovador; }
    public ApprovalDecision getDecisao() { return decisao; }
    public LocalDateTime getDecididoEm() { return decididoEm; }
    public String getComentario() { return comentario; }
    public String getAssinaturaEletronica() { return assinaturaEletronica; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setStage(ApprovalStage stage) { this.stage = stage; }
    public void setAprovador(User aprovador) { this.aprovador = aprovador; }
    public void setDecisao(ApprovalDecision decisao) { this.decisao = decisao; }
    public void setDecididoEm(LocalDateTime decididoEm) { this.decididoEm = decididoEm; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public void setAssinaturaEletronica(String assinaturaEletronica) { this.assinaturaEletronica = assinaturaEletronica; }
}
