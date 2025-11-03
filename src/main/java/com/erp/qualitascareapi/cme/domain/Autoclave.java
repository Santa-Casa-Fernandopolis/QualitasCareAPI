package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Audited
@Entity
@Table(name = "cme_autoclaves")
public class Autoclave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 120)
    private String fabricante;

    @Column(length = 120)
    private String modelo;

    @Column(length = 120)
    private String numeroSerie;

    @Column(length = 120)
    private String localizacao;

    @Column
    private LocalDate ultimaHigienizacaoProfunda;

    @Column(nullable = false)
    private Boolean ativo = Boolean.TRUE;

    public Autoclave() {
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public LocalDate getUltimaHigienizacaoProfunda() {
        return ultimaHigienizacaoProfunda;
    }

    public void setUltimaHigienizacaoProfunda(LocalDate ultimaHigienizacaoProfunda) {
        this.ultimaHigienizacaoProfunda = ultimaHigienizacaoProfunda;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
