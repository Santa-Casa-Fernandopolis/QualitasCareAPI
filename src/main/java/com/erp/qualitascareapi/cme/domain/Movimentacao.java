package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.MovimentacaoTipo;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Audited
@Entity
@Table(name = "cme_movimentacoes")
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private LoteEtiqueta lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_origem_id")
    private Setor setorOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_destino_id")
    private Setor setorDestino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MovimentacaoTipo tipo;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private User responsavel;

    @Column(length = 255)
    private String observacoes;

    public Movimentacao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public LoteEtiqueta getLote() {
        return lote;
    }

    public void setLote(LoteEtiqueta lote) {
        this.lote = lote;
    }

    public Setor getSetorOrigem() {
        return setorOrigem;
    }

    public void setSetorOrigem(Setor setorOrigem) {
        this.setorOrigem = setorOrigem;
    }

    public Setor getSetorDestino() {
        return setorDestino;
    }

    public void setSetorDestino(Setor setorDestino) {
        this.setorDestino = setorDestino;
    }

    public MovimentacaoTipo getTipo() {
        return tipo;
    }

    public void setTipo(MovimentacaoTipo tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public User getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(User responsavel) {
        this.responsavel = responsavel;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
