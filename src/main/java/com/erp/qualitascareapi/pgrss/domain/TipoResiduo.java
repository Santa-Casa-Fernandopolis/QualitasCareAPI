package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.pgrss.enums.TipoAcondicionamento;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.util.Objects;

@Audited
@Entity
@Table(name = "pgrss_tipos_residuo",
        indexes = {
                @Index(name = "ix_pgrss_tr_tenant_grupo", columnList = "tenant_id,grupo_id")
        })
public class TipoResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    private GrupoResiduo grupo;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 300)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acondicionamento", length = 30)
    private TipoAcondicionamento tipoAcondicionamento;

    @Column(name = "requer_identificacao", nullable = false)
    private boolean requerIdentificacao = false;

    @Column(name = "requer_pesagem", nullable = false)
    private boolean requerPesagem = true;

    @Column(nullable = false)
    private boolean ativo = true;

    public TipoResiduo() {}

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public GrupoResiduo getGrupo() { return grupo; }
    public void setGrupo(GrupoResiduo grupo) { this.grupo = grupo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public TipoAcondicionamento getTipoAcondicionamento() { return tipoAcondicionamento; }
    public void setTipoAcondicionamento(TipoAcondicionamento tipoAcondicionamento) { this.tipoAcondicionamento = tipoAcondicionamento; }
    public boolean isRequerIdentificacao() { return requerIdentificacao; }
    public void setRequerIdentificacao(boolean requerIdentificacao) { this.requerIdentificacao = requerIdentificacao; }
    public boolean isRequerPesagem() { return requerPesagem; }
    public void setRequerPesagem(boolean requerPesagem) { this.requerPesagem = requerPesagem; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public boolean equals(Object o) { return o instanceof TipoResiduo t && Objects.equals(id, t.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
