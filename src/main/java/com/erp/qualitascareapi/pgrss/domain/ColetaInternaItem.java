package com.erp.qualitascareapi.pgrss.domain;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.util.Objects;

@Audited
@Entity
@Table(name = "pgrss_coleta_interna_items",
        indexes = {
                @Index(name = "ix_pgrss_cii_coleta", columnList = "coleta_interna_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_pgrss_cii_pesagem", columnNames = {"pesagem_id"})
        })
public class ColetaInternaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "coleta_interna_id", nullable = false)
    private ColetaInterna coletaInterna;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pesagem_id", nullable = false)
    private PesagemResiduo pesagem;

    public ColetaInternaItem() {}

    public Long getId() { return id; }
    public ColetaInterna getColetaInterna() { return coletaInterna; }
    public void setColetaInterna(ColetaInterna coletaInterna) { this.coletaInterna = coletaInterna; }
    public PesagemResiduo getPesagem() { return pesagem; }
    public void setPesagem(PesagemResiduo pesagem) { this.pesagem = pesagem; }

    @Override
    public boolean equals(Object o) { return o instanceof ColetaInternaItem c && Objects.equals(id, c.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
