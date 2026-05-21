package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.pgrss.enums.GrupoResiduoCodigo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.util.Objects;

/**
 * Grupos de resíduos conforme RDC ANVISA nº 222/2018:
 * Grupo A (Biológicos), B (Químicos), C (Radioativos), D (Comuns), E (Perfurocortantes).
 */
@Audited
@Entity
@Table(name = "pgrss_grupos_residuo",
        uniqueConstraints = @UniqueConstraint(name = "uk_pgrss_gr_tenant_codigo",
                columnNames = {"tenant_id", "codigo"}),
        indexes = {
                @Index(name = "ix_pgrss_gr_tenant", columnList = "tenant_id")
        })
public class GrupoResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private GrupoResiduoCodigo codigo;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    /** Cor de identificação no padrão hex, ex: #FF0000 (vermelho para Grupo A). */
    @Column(length = 10)
    private String corIdentificacao;

    @Column(nullable = false)
    private Boolean ativo = Boolean.TRUE;

    public GrupoResiduo() {}

    public Long getId() { return id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public GrupoResiduoCodigo getCodigo() { return codigo; }
    public void setCodigo(GrupoResiduoCodigo codigo) { this.codigo = codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCorIdentificacao() { return corIdentificacao; }
    public void setCorIdentificacao(String corIdentificacao) { this.corIdentificacao = corIdentificacao; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    @Override
    public boolean equals(Object o) { return o instanceof GrupoResiduo g && Objects.equals(id, g.id); }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
