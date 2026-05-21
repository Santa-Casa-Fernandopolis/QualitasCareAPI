package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.pgrss.enums.SetorGeradorTipo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.envers.Audited;

import java.util.Objects;

@Audited
@Entity
@Table(name = "pgrss_setores_geradores",
        indexes = {
                @Index(name = "ix_pgrss_sg_tenant_ativo", columnList = "tenant_id,ativo"),
                @Index(name = "ix_pgrss_sg_tenant_tipo", columnList = "tenant_id,tipo")
        })
public class SetorGerador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /** Referência opcional ao setor IAM correspondente. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private Setor setor;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 40)
    private String codigoInterno;

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private SetorGeradorTipo tipo;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private Boolean ativo = Boolean.TRUE;

    public SetorGerador() {}

    public Long getId() { return id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Setor getSetor() { return setor; }
    public void setSetor(Setor setor) { this.setor = setor; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCodigoInterno() { return codigoInterno; }
    public void setCodigoInterno(String codigoInterno) { this.codigoInterno = codigoInterno; }

    public SetorGeradorTipo getTipo() { return tipo; }
    public void setTipo(SetorGeradorTipo tipo) { this.tipo = tipo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    @Override
    public boolean equals(Object o) { return o instanceof SetorGerador s && Objects.equals(id, s.id); }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
