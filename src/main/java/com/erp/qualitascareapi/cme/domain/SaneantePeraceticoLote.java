package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.Objects;

/** ---------------- SaneantePeraceticoLote ---------------- */
@Audited
@Entity
@Table(name = "cme_saneante_lotes",
        uniqueConstraints = @UniqueConstraint(name = "uk_saneante_tenant_lote", columnNames = {"tenant_id","numero_lote"}),
        indexes = @Index(name = "ix_saneante_tenant_validade", columnList = "tenant_id,data_validade"))
public class SaneantePeraceticoLote {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotBlank
    @Column(name = "numero_lote", nullable = false, length = 80)
    private String numeroLote;

    @Column(length = 120)
    private String fabricante;

    @Column(length = 60)
    private String concentracao;

    @NotNull
    @Column(name = "data_validade", nullable = false)
    private LocalDate dataValidade;

    @Column(name = "data_abertura")
    private LocalDate dataAbertura;

    @Positive
    @Column(name = "volume_inicial_ml")
    private Double volumeInicialMl;

    @Column(length = 800)
    private String observacoes;

    // getters/setters/equals/hashCode
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }
    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }
    public String getConcentracao() { return concentracao; }
    public void setConcentracao(String concentracao) { this.concentracao = concentracao; }
    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }
    public LocalDate getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDate dataAbertura) { this.dataAbertura = dataAbertura; }
    public Double getVolumeInicialMl() { return volumeInicialMl; }
    public void setVolumeInicialMl(Double volumeInicialMl) { this.volumeInicialMl = volumeInicialMl; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override public boolean equals(Object o){ return o instanceof SaneantePeraceticoLote s && Objects.equals(id, s.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
