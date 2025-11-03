package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.UsoSaneanteEtapa;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Audited
@Entity
@Table(name = "cme_usos_saneante")
public class UsoSaneante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lote_saneante_id", nullable = false)
    private SaneantePeraceticoLote loteSaneante;

    @Column(nullable = false)
    private LocalDate dataUso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private UsoSaneanteEtapa etapa;

    @Column
    private Double volumeUtilizadoMl;

    @Column(length = 120)
    private String diluicao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usado_por_id")
    private User usadoPor;

    @Column(length = 255)
    private String observacoes;

    public UsoSaneante() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SaneantePeraceticoLote getLoteSaneante() {
        return loteSaneante;
    }

    public void setLoteSaneante(SaneantePeraceticoLote loteSaneante) {
        this.loteSaneante = loteSaneante;
    }

    public LocalDate getDataUso() {
        return dataUso;
    }

    public void setDataUso(LocalDate dataUso) {
        this.dataUso = dataUso;
    }

    public UsoSaneanteEtapa getEtapa() {
        return etapa;
    }

    public void setEtapa(UsoSaneanteEtapa etapa) {
        this.etapa = etapa;
    }

    public Double getVolumeUtilizadoMl() {
        return volumeUtilizadoMl;
    }

    public void setVolumeUtilizadoMl(Double volumeUtilizadoMl) {
        this.volumeUtilizadoMl = volumeUtilizadoMl;
    }

    public String getDiluicao() {
        return diluicao;
    }

    public void setDiluicao(String diluicao) {
        this.diluicao = diluicao;
    }

    public User getUsadoPor() {
        return usadoPor;
    }

    public void setUsadoPor(User usadoPor) {
        this.usadoPor = usadoPor;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
