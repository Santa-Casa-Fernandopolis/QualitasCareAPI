package com.erp.qualitascareapi.iam.domain;

import com.erp.qualitascareapi.iam.enums.TipoSetor;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "iam_setores",
        uniqueConstraints = @UniqueConstraint(name = "uq_setor_tenant_nome", columnNames = {"tenant_id", "nome"}),
        indexes = {
                @Index(name = "ix_setor_tenant_tipo", columnList = "tenant_id,tipo"),
                @Index(name = "ix_setor_tenant_tipo_cadastro", columnList = "tenant_id,tipo_setor_id"),
                @Index(name = "ix_setor_tenant_especialidade", columnList = "tenant_id,especialidade_id")
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
    @Column(length = 30)
    private TipoSetor tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_setor_id")
    private SetorTipoCadastro tipoCadastro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidade_id")
    private SetorEspecialidade especialidade;

    @Column(length = 255)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private User supervisor;

    public Setor() {
    }
    public Setor(Long id, Tenant tenant, String nome, TipoSetor tipo, String descricao) {
        this.id = id;
        this.tenant = tenant;
        this.nome = nome;
        this.tipo = tipo;
        this.descricao = descricao;
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

    public SetorTipoCadastro getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(SetorTipoCadastro tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public SetorEspecialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(SetorEspecialidade especialidade) {
        this.especialidade = especialidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public User getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(User supervisor) {
        this.supervisor = supervisor;
    }
}
