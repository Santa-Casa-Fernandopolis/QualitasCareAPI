package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.MovimentacaoTipo;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.Objects;

/** ---------------- MovimentacaoCME ---------------- */
@Audited
@Entity
@Table(name = "cme_movimentacoes",
        indexes = {
                @Index(name = "ix_mov_tenant_lote_data", columnList = "tenant_id,lote_id,data_hora"),
                @Index(name = "ix_mov_tenant_tipo", columnList = "tenant_id,tipo")
        })
public class MovimentacaoCME {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteEtiqueta lote;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "setor_origem_id")
    private Setor setorOrigem;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "setor_destino_id")
    private Setor setorDestino;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private MovimentacaoTipo tipo;

    @NotNull @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "responsavel_id")
    private User responsavel;

    @Column(length = 800)
    private String observacoes;

    // getters/setters/equals/hashCode
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public LoteEtiqueta getLote() { return lote; }
    public void setLote(LoteEtiqueta lote) { this.lote = lote; }
    public Setor getSetorOrigem() { return setorOrigem; }
    public void setSetorOrigem(Setor setorOrigem) { this.setorOrigem = setorOrigem; }
    public Setor getSetorDestino() { return setorDestino; }
    public void setSetorDestino(Setor setorDestino) { this.setorDestino = setorDestino; }
    public MovimentacaoTipo getTipo() { return tipo; }
    public void setTipo(MovimentacaoTipo tipo) { this.tipo = tipo; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public User getResponsavel() { return responsavel; }
    public void setResponsavel(User responsavel) { this.responsavel = responsavel; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override public boolean equals(Object o){ return o instanceof MovimentacaoCME m && Objects.equals(id, m.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
