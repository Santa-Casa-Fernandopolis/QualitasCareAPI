package com.erp.qualitascareapi.cme.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "cme_kit_fisico_instrumentos",
        indexes = {
                @Index(name = "ix_cme_kit_fis_instr_kit", columnList = "kit_fisico_id"),
                @Index(name = "ix_cme_kit_fis_instr_instr", columnList = "instrumento_fisico_id")
        })
public class KitFisicoInstrumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kit_fisico_id", nullable = false)
    private KitFisico kitFisico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrumento_fisico_id", nullable = false)
    private InstrumentoFisico instrumentoFisico;

    @Column(name = "vinculado_em", nullable = false)
    private LocalDate vinculadoEm = LocalDate.now();

    @Column(name = "desvinculado_em")
    private LocalDate desvinculadoEm;

    @Column(length = 500)
    private String observacoes;

    @Column(nullable = false)
    private Boolean ativo = Boolean.TRUE;

    public Long getId() { return id; }
    public KitFisico getKitFisico() { return kitFisico; }
    public void setKitFisico(KitFisico kitFisico) { this.kitFisico = kitFisico; }
    public InstrumentoFisico getInstrumentoFisico() { return instrumentoFisico; }
    public void setInstrumentoFisico(InstrumentoFisico instrumentoFisico) { this.instrumentoFisico = instrumentoFisico; }
    public LocalDate getVinculadoEm() { return vinculadoEm; }
    public void setVinculadoEm(LocalDate vinculadoEm) { this.vinculadoEm = vinculadoEm; }
    public LocalDate getDesvinculadoEm() { return desvinculadoEm; }
    public void setDesvinculadoEm(LocalDate desvinculadoEm) { this.desvinculadoEm = desvinculadoEm; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
