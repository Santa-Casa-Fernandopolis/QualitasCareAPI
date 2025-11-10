package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name="edu_training_providers")
public class TrainingProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @Column(nullable=false, length=160)
    private String nome;

    @Column(length=18)
    private String cnpj;

    @Column(length=160)
    private String contatoEmail;

    @Column(length=40)
    private String contatoTelefone;

    @Column(nullable=false)
    private Boolean interno;

    @Column(length=255)
    private String siteUrl;

    @Column(columnDefinition = "text")
    private String observacoes;

    public TrainingProvider() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getContatoEmail() { return contatoEmail; }
    public void setContatoEmail(String contatoEmail) { this.contatoEmail = contatoEmail; }
    public String getContatoTelefone() { return contatoTelefone; }
    public void setContatoTelefone(String contatoTelefone) { this.contatoTelefone = contatoTelefone; }
    public Boolean getInterno() { return interno; }
    public void setInterno(Boolean interno) { this.interno = interno; }
    public String getSiteUrl() { return siteUrl; }
    public void setSiteUrl(String siteUrl) { this.siteUrl = siteUrl; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
