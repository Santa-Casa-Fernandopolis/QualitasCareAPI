package com.erp.qualitascareapi.environmental.domain;

import com.erp.qualitascareapi.environmental.enums.TipoUsoGeladeira;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.util.Objects;

/**
 * Cadastro de geladeiras utilizadas para conservação de medicamentos,
 * vacinas, hemoderivados e outros imunobiológicos.
 *
 * Cada geladeira possui uma faixa de temperatura alvo (mínima e máxima)
 * que serve como parâmetro para avaliação dos registros periódicos de temperatura.
 *
 * Normas de referência: ANVISA RDC 430/2020, PNI / Ministério da Saúde (cadeia fria de vacinas).
 */
@Audited
@Entity
@Table(name = "env_geladeiras_medicamentos",
        indexes = {
                @Index(name = "ix_env_gel_tenant_ativo", columnList = "tenant_id,ativo"),
                @Index(name = "ix_env_gel_tenant_tipo", columnList = "tenant_id,tipo_uso")
        })
public class GeladeiraMedicamentos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /** Nome ou identificador da geladeira (ex.: "GEL-01 — Vacinas UTI"). */
    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "tipo_uso", nullable = false, length = 30)
    private TipoUsoGeladeira tipoUso;

    /** Sala ou setor onde o equipamento está instalado. */
    @Column(name = "local_sala", length = 120)
    private String localSala;

    @Column(length = 100)
    private String fabricante;

    @Column(length = 100)
    private String modelo;

    @Column(name = "numero_serie", length = 80)
    private String numeroSerie;

    /** Temperatura mínima aceitável (°C) — alerta abaixo deste valor. */
    @Column(name = "temperatura_min_celsius")
    private Double temperaturaMinCelsius;

    /** Temperatura máxima aceitável (°C) — alerta acima deste valor. */
    @Column(name = "temperatura_max_celsius")
    private Double temperaturaMaxCelsius;

    /** Frequência esperada de leituras (em horas); ex.: 12 = a cada 12 h. */
    @Column(name = "frequencia_leitura_horas")
    private Integer frequenciaLeituraHoras;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(length = 255)
    private String observacoes;

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public TipoUsoGeladeira getTipoUso() { return tipoUso; }
    public void setTipoUso(TipoUsoGeladeira tipoUso) { this.tipoUso = tipoUso; }
    public String getLocalSala() { return localSala; }
    public void setLocalSala(String localSala) { this.localSala = localSala; }
    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public Double getTemperaturaMinCelsius() { return temperaturaMinCelsius; }
    public void setTemperaturaMinCelsius(Double temperaturaMinCelsius) { this.temperaturaMinCelsius = temperaturaMinCelsius; }
    public Double getTemperaturaMaxCelsius() { return temperaturaMaxCelsius; }
    public void setTemperaturaMaxCelsius(Double temperaturaMaxCelsius) { this.temperaturaMaxCelsius = temperaturaMaxCelsius; }
    public Integer getFrequenciaLeituraHoras() { return frequenciaLeituraHoras; }
    public void setFrequenciaLeituraHoras(Integer frequenciaLeituraHoras) { this.frequenciaLeituraHoras = frequenciaLeituraHoras; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public boolean equals(Object o) { return o instanceof GeladeiraMedicamentos g && Objects.equals(id, g.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
