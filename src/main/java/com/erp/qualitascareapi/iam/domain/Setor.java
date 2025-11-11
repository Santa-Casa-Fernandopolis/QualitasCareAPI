package com.erp.qualitascareapi.iam.domain;

import com.erp.qualitascareapi.iam.enums.TipoSetor;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "iam_setores",
        uniqueConstraints = @UniqueConstraint(name = "uq_setor_tenant_nome", columnNames = {"tenant_id", "nome"}),
        indexes = {
                @Index(name = "ix_setor_tenant_tipo", columnList = "tenant_id,tipo")
        })
public class Setor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoSetor tipo;

    @Column(length = 255)
    private String descricao;

    public Setor() {
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

    public TipoSetor getTipo() {
        return tipo;
    }

    public void setTipo(TipoSetor tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
