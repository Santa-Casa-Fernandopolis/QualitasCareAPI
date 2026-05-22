package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.pgrss.enums.RiscoResiduo;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.util.Objects;

@Audited
@Entity
@Table(name = "pgrss_grupos_residuo",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_pgrss_gr_tenant_codigo", columnNames = {"tenant_id", "codigo"})
        },
        indexes = {
                @Index(name = "ix_pgrss_gr_tenant", columnList = "tenant_id")
        })
public class GrupoResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 5)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RiscoResiduo risco;

    @Column(name = "padrao_cor_identificacao", length = 60)
    private String padraoCorIdentificacao;

    @Column(name = "requer_tratamento", nullable = false)
    private boolean requerTratamento = false;

    @Column(nullable = false)
    private boolean ativo = true;

    public GrupoResiduo() {}

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public RiscoResiduo getRisco() { return risco; }
    public void setRisco(RiscoResiduo risco) { this.risco = risco; }
    public String getPadraoCorIdentificacao() { return padraoCorIdentificacao; }
    public void setPadraoCorIdentificacao(String padraoCorIdentificacao) { this.padraoCorIdentificacao = padraoCorIdentificacao; }
    public boolean isRequerTratamento() { return requerTratamento; }
    public void setRequerTratamento(boolean requerTratamento) { this.requerTratamento = requerTratamento; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public boolean equals(Object o) { return o instanceof GrupoResiduo g && Objects.equals(id, g.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
