package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.UsoSaneanteEtapa;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.Objects;

/** ---------------- UsoSaneante ---------------- */
@Audited
@Entity
@Table(name = "cme_uso_saneante",
        indexes = {
                @Index(name = "ix_uso_lote_data", columnList = "lote_saneante_id,data_uso"),
                @Index(name = "ix_uso_etapa", columnList = "etapa")
        })
public class UsoSaneante {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_saneante_id", nullable = false)
    private SaneantePeraceticoLote loteSaneante;

    @NotNull
    @Column(name = "data_uso", nullable = false)
    private LocalDate dataUso;

    @NotNull @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UsoSaneanteEtapa etapa;

    @Positive
    @Column(name = "volume_utilizado_ml")
    private Double volumeUtilizadoMl;

    @Column(length = 120)
    private String diluicao;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "usado_por_id")
    private User usadoPor;

    @Column(length = 800)
    private String observacoes;

    // getters/setters/equals/hashCode
    public Long getId() { return id; }
    public SaneantePeraceticoLote getLoteSaneante() { return loteSaneante; }
    public void setLoteSaneante(SaneantePeraceticoLote loteSaneante) { this.loteSaneante = loteSaneante; }
    public LocalDate getDataUso() { return dataUso; }
    public void setDataUso(LocalDate dataUso) { this.dataUso = dataUso; }
    public UsoSaneanteEtapa getEtapa() { return etapa; }
    public void setEtapa(UsoSaneanteEtapa etapa) { this.etapa = etapa; }
    public Double getVolumeUtilizadoMl() { return volumeUtilizadoMl; }
    public void setVolumeUtilizadoMl(Double volumeUtilizadoMl) { this.volumeUtilizadoMl = volumeUtilizadoMl; }
    public String getDiluicao() { return diluicao; }
    public void setDiluicao(String diluicao) { this.diluicao = diluicao; }
    public User getUsadoPor() { return usadoPor; }
    public void setUsadoPor(User usadoPor) { this.usadoPor = usadoPor; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override public boolean equals(Object o){ return o instanceof UsoSaneante u && Objects.equals(id, u.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
