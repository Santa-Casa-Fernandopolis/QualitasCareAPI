package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.CmeEtapaTipo;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.util.Objects;

@Audited
@Entity
@Table(name = "cme_etapas_catalogo",
        uniqueConstraints = @UniqueConstraint(name = "uk_cme_etapa_catalogo_codigo",
                columnNames = {"tenant_id", "codigo"}),
        indexes = @Index(name = "ix_cme_etapa_catalogo_tenant_nome", columnList = "tenant_id,nome"))
public class CmeEtapaCatalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String codigo;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_etapa", nullable = false, length = 30)
    private CmeEtapaTipo tipoEtapa;

    @Column(nullable = false)
    private boolean obrigatoria = true;

    @Column(name = "permite_pular", nullable = false)
    private boolean permitePular = false;

    @Column(name = "exige_evidencia", nullable = false)
    private boolean exigeEvidencia = false;

    @Column(name = "exige_aprovacao", nullable = false)
    private boolean exigeAprovacao = false;

    @Column(name = "rota_destino", length = 120)
    private String rotaDestino;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(length = 255)
    private String observacoes;

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public CmeEtapaTipo getTipoEtapa() { return tipoEtapa; }
    public void setTipoEtapa(CmeEtapaTipo tipoEtapa) { this.tipoEtapa = tipoEtapa; }
    public boolean isObrigatoria() { return obrigatoria; }
    public void setObrigatoria(boolean obrigatoria) { this.obrigatoria = obrigatoria; }
    public boolean isPermitePular() { return permitePular; }
    public void setPermitePular(boolean permitePular) { this.permitePular = permitePular; }
    public boolean isExigeEvidencia() { return exigeEvidencia; }
    public void setExigeEvidencia(boolean exigeEvidencia) { this.exigeEvidencia = exigeEvidencia; }
    public boolean isExigeAprovacao() { return exigeAprovacao; }
    public void setExigeAprovacao(boolean exigeAprovacao) { this.exigeAprovacao = exigeAprovacao; }
    public String getRotaDestino() { return rotaDestino; }
    public void setRotaDestino(String rotaDestino) { this.rotaDestino = rotaDestino; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public boolean equals(Object o) { return o instanceof CmeEtapaCatalogo e && Objects.equals(id, e.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
