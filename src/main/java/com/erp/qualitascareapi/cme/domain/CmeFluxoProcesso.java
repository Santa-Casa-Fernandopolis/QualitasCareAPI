package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.TipoFluxoCME;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Audited
@Entity
@Table(name = "cme_fluxos_processo",
        uniqueConstraints = @UniqueConstraint(name = "uk_cme_fluxo_tenant_tipo_versao",
                columnNames = {"tenant_id", "tipo_fluxo", "numero_versao"}),
        indexes = {
                @Index(name = "ix_cme_fluxo_tenant_tipo_ativo", columnList = "tenant_id,tipo_fluxo,ativo"),
                @Index(name = "ix_cme_fluxo_tenant_nome", columnList = "tenant_id,nome")
        })
public class CmeFluxoProcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_fluxo", nullable = false, length = 20)
    private TipoFluxoCME tipoFluxo;

    @NotNull
    @Column(name = "numero_versao", nullable = false)
    private Integer numeroVersao = 1;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "data_vigencia_inicio")
    private LocalDate dataVigenciaInicio;

    @Column(name = "data_vigencia_fim")
    private LocalDate dataVigenciaFim;

    @Column(length = 255)
    private String observacoes;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @OneToMany(mappedBy = "fluxoProcesso", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<CmeEtapaProcesso> etapas = new ArrayList<>();

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public TipoFluxoCME getTipoFluxo() { return tipoFluxo; }
    public void setTipoFluxo(TipoFluxoCME tipoFluxo) { this.tipoFluxo = tipoFluxo; }
    public Integer getNumeroVersao() { return numeroVersao; }
    public void setNumeroVersao(Integer numeroVersao) { this.numeroVersao = numeroVersao; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public LocalDate getDataVigenciaInicio() { return dataVigenciaInicio; }
    public void setDataVigenciaInicio(LocalDate dataVigenciaInicio) { this.dataVigenciaInicio = dataVigenciaInicio; }
    public LocalDate getDataVigenciaFim() { return dataVigenciaFim; }
    public void setDataVigenciaFim(LocalDate dataVigenciaFim) { this.dataVigenciaFim = dataVigenciaFim; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public List<CmeEtapaProcesso> getEtapas() { return etapas; }

    @Override
    public boolean equals(Object o) { return o instanceof CmeFluxoProcesso f && Objects.equals(id, f.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
