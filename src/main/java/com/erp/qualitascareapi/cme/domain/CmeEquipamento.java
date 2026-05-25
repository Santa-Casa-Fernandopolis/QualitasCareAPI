package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.CmeEtapaTipo;
import com.erp.qualitascareapi.cme.enums.CmeTipoEquipamento;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Audited
@Entity
@Table(name = "cme_equipamentos",
        uniqueConstraints = @UniqueConstraint(name = "uk_cme_equipamento_codigo", columnNames = {"tenant_id", "codigo"}))
public class CmeEquipamento {
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
    @Column(name = "tipo_equipamento", nullable = false, length = 40)
    private CmeTipoEquipamento tipoEquipamento;

    @ElementCollection(targetClass = CmeEtapaTipo.class)
    @CollectionTable(name = "cme_equipamento_etapas", joinColumns = @JoinColumn(name = "equipamento_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_etapa", nullable = false, length = 30)
    private Set<CmeEtapaTipo> etapasPermitidas = new HashSet<>();

    @Column(length = 80)
    private String fabricante;

    @Column(length = 80)
    private String modelo;

    @Column(name = "numero_serie", length = 80)
    private String numeroSerie;

    @Column(length = 120)
    private String localizacao;

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
    public CmeTipoEquipamento getTipoEquipamento() { return tipoEquipamento; }
    public void setTipoEquipamento(CmeTipoEquipamento tipoEquipamento) { this.tipoEquipamento = tipoEquipamento; }
    public Set<CmeEtapaTipo> getEtapasPermitidas() { return etapasPermitidas; }
    public void setEtapasPermitidas(Set<CmeEtapaTipo> etapasPermitidas) { this.etapasPermitidas = etapasPermitidas; }
    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override public boolean equals(Object o) { return o instanceof CmeEquipamento e && Objects.equals(id, e.id); }
    @Override public int hashCode() { return Objects.hashCode(id); }
}
