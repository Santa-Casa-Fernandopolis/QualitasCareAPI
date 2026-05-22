package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.approval.core.domain.ApprovalFlowDef;
import com.erp.qualitascareapi.cme.enums.StatusAprovacaoCme;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.Objects;

@Audited
@Entity
@Table(name = "cme_aprovacoes_lote_inalatorio",
        indexes = {
                @Index(name = "ix_aprov_lote_tenant_lote", columnList = "tenant_id,lote_id"),
                @Index(name = "ix_aprov_lote_tenant_status", columnList = "tenant_id,status")
        })
public class AprovacaoLoteInalatorio {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteEtiqueta lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flow_def_id")
    private ApprovalFlowDef flowDef;

    @Column(name = "tipo_material", length = 80)
    private String tipoMaterial;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAprovacaoCme status = StatusAprovacaoCme.PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprovado_por_id")
    private User aprovadoPor;

    @Column(name = "data_aprovacao")
    private LocalDateTime dataAprovacao;

    @Column(length = 800)
    private String comentario;

    public Long getId()                                      { return id; }
    public Tenant getTenant()                                { return tenant; }
    public void setTenant(Tenant tenant)                     { this.tenant = tenant; }
    public LoteEtiqueta getLote()                            { return lote; }
    public void setLote(LoteEtiqueta lote)                   { this.lote = lote; }
    public ApprovalFlowDef getFlowDef()                      { return flowDef; }
    public void setFlowDef(ApprovalFlowDef flowDef)          { this.flowDef = flowDef; }
    public String getTipoMaterial()                          { return tipoMaterial; }
    public void setTipoMaterial(String tipoMaterial)         { this.tipoMaterial = tipoMaterial; }
    public StatusAprovacaoCme getStatus()                    { return status; }
    public void setStatus(StatusAprovacaoCme status)         { this.status = status; }
    public User getAprovadoPor()                             { return aprovadoPor; }
    public void setAprovadoPor(User aprovadoPor)             { this.aprovadoPor = aprovadoPor; }
    public LocalDateTime getDataAprovacao()                  { return dataAprovacao; }
    public void setDataAprovacao(LocalDateTime d)            { this.dataAprovacao = d; }
    public String getComentario()                            { return comentario; }
    public void setComentario(String comentario)             { this.comentario = comentario; }

    @Override public boolean equals(Object o) { return o instanceof AprovacaoLoteInalatorio a && Objects.equals(id, a.id); }
    @Override public int hashCode()           { return Objects.hashCode(id); }
}
