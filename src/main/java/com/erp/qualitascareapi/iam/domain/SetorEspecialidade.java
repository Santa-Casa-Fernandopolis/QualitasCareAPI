package com.erp.qualitascareapi.iam.domain;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "iam_setor_especialidades",
        uniqueConstraints = @UniqueConstraint(name = "uq_setor_especialidade_tenant_nome", columnNames = {"tenant_id", "nome"}),
        indexes = {
                @Index(name = "ix_setor_especialidade_tenant_active", columnList = "tenant_id,active"),
                @Index(name = "ix_setor_especialidade_tipo", columnList = "tipo_setor_id")
        })
public class SetorEspecialidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_setor_id")
    private SetorTipoCadastro tipoSetor;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false)
    private boolean active = true;

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

    public SetorTipoCadastro getTipoSetor() {
        return tipoSetor;
    }

    public void setTipoSetor(SetorTipoCadastro tipoSetor) {
        this.tipoSetor = tipoSetor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

