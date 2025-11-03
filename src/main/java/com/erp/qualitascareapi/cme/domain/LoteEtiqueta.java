package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.LoteStatus;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Audited
@Entity
@Table(name = "cme_lotes_etiqueta",
        uniqueConstraints = @UniqueConstraint(name = "uq_cme_lote_codigo", columnNames = {"tenant_id", "codigo"}))
public class LoteEtiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 60)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_versao_id")
    private KitVersion kitVersao;

    @Column
    private LocalDate dataEmpacotamento;

    @Column
    private LocalDate validade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoteStatus status = LoteStatus.MONTADO;

    @Column(length = 120)
    private String qrCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "montado_por_id")
    private User montadoPor;

    @Column(length = 255)
    private String observacoes;

    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    public LoteEtiqueta() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public KitVersion getKitVersao() {
        return kitVersao;
    }

    public void setKitVersao(KitVersion kitVersao) {
        this.kitVersao = kitVersao;
    }

    public LocalDate getDataEmpacotamento() {
        return dataEmpacotamento;
    }

    public void setDataEmpacotamento(LocalDate dataEmpacotamento) {
        this.dataEmpacotamento = dataEmpacotamento;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public LoteStatus getStatus() {
        return status;
    }

    public void setStatus(LoteStatus status) {
        this.status = status;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public User getMontadoPor() {
        return montadoPor;
    }

    public void setMontadoPor(User montadoPor) {
        this.montadoPor = montadoPor;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
