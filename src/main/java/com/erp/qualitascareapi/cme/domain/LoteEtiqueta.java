package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.*;
import com.erp.qualitascareapi.approval.core.contracts.ApprovableTarget;
import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.core.domain.KitVersion;
import com.erp.qualitascareapi.core.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/** ---------------- LoteEtiqueta ---------------- */
@Audited
@Entity
@Table(name = "cme_lotes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_lote_tenant_codigo", columnNames = {"tenant_id","codigo"}),
                @UniqueConstraint(name = "uk_lote_tenant_qrcode", columnNames = {"tenant_id","qr_code"})
        },
        indexes = {
                @Index(name = "ix_lote_tenant_status", columnList = "tenant_id,status"),
                @Index(name = "ix_lote_tenant_kit_versao", columnList = "tenant_id,kit_versao_id")
        })
public class LoteEtiqueta {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotBlank @Column(nullable = false, length = 60)
    private String codigo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_versao_id", nullable = false)
    private KitVersion kitVersao;

    @NotNull @Column(nullable = false)
    private LocalDate dataEmpacotamento;

    @NotNull @Column(nullable = false)
    private LocalDate validade;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoteStatus status = LoteStatus.MONTADO;

    @NotBlank @Column(name = "qr_code", nullable = false, length = 120)
    private String qrCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "montado_por_id")
    private User montadoPor;

    @Column(length = 800)
    private String observacoes;

    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    // getters/setters/equals/hashCode
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public KitVersion getKitVersao() { return kitVersao; }
    public void setKitVersao(KitVersion kitVersao) { this.kitVersao = kitVersao; }
    public LocalDate getDataEmpacotamento() { return dataEmpacotamento; }
    public void setDataEmpacotamento(LocalDate dataEmpacotamento) { this.dataEmpacotamento = dataEmpacotamento; }
    public LocalDate getValidade() { return validade; }
    public void setValidade(LocalDate validade) { this.validade = validade; }
    public LoteStatus getStatus() { return status; }
    public void setStatus(LoteStatus status) { this.status = status; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public User getMontadoPor() { return montadoPor; }
    public void setMontadoPor(User montadoPor) { this.montadoPor = montadoPor; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public LocalDateTime getCriadoEm() { return criadoEm; }

    @Override public boolean equals(Object o){ return o instanceof LoteEtiqueta l && Objects.equals(id, l.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
