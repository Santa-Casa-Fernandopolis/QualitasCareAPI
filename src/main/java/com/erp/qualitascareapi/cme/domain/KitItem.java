package com.erp.qualitascareapi.cme.domain;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "cme_kits_itens")
public class KitItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "versao_id", nullable = false)
    private KitVersion versao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrumento_id", nullable = false)
    private Instrumento instrumento;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(length = 255)
    private String observacoes;

    public KitItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public KitVersion getVersao() {
        return versao;
    }

    public void setVersao(KitVersion versao) {
        this.versao = versao;
    }

    public Instrumento getInstrumento() {
        return instrumento;
    }

    public void setInstrumento(Instrumento instrumento) {
        this.instrumento = instrumento;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
