package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.pgrss.enums.TipoSetorGerador;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.util.Objects;

@Audited
@Entity
@Table(name = "pgrss_setores_geradores",
        indexes = {
                @Index(name = "ix_pgrss_sg_tenant_ativo", columnList = "tenant_id,ativo")
        })
public class SetorGerador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(name = "centro_custo", length = 30)
    private String centroCusto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoSetorGerador tipo;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(length = 255)
    private String observacoes;

    public SetorGerador() {}

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCentroCusto() { return centroCusto; }
    public void setCentroCusto(String centroCusto) { this.centroCusto = centroCusto; }
    public TipoSetorGerador getTipo() { return tipo; }
    public void setTipo(TipoSetorGerador tipo) { this.tipo = tipo; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public boolean equals(Object o) { return o instanceof SetorGerador s && Objects.equals(id, s.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
