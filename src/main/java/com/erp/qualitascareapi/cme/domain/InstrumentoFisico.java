package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.IdentificacaoFisicaStatus;
import com.erp.qualitascareapi.core.domain.Instrumento;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;

@Entity
@Table(name = "cme_instrumentos_fisicos",
        uniqueConstraints = @UniqueConstraint(name = "uk_cme_instr_fis_tenant_ident", columnNames = {"tenant_id", "identificador_unico"}),
        indexes = {
                @Index(name = "ix_cme_instr_fis_tenant_status", columnList = "tenant_id,status"),
                @Index(name = "ix_cme_instr_fis_instr", columnList = "instrumento_id")
        })
public class InstrumentoFisico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrumento_id", nullable = false)
    private Instrumento instrumento;

    @Column(name = "identificador_unico", nullable = false, length = 80)
    private String identificadorUnico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IdentificacaoFisicaStatus status = IdentificacaoFisicaStatus.ATIVO;

    @Column(length = 120)
    private String localizacao;

    @Column(length = 800)
    private String observacoes;

    @Column(nullable = false)
    private Boolean ativo = Boolean.TRUE;

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public Instrumento getInstrumento() { return instrumento; }
    public void setInstrumento(Instrumento instrumento) { this.instrumento = instrumento; }
    public String getIdentificadorUnico() { return identificadorUnico; }
    public void setIdentificadorUnico(String identificadorUnico) { this.identificadorUnico = identificadorUnico; }
    public IdentificacaoFisicaStatus getStatus() { return status; }
    public void setStatus(IdentificacaoFisicaStatus status) { this.status = status; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
