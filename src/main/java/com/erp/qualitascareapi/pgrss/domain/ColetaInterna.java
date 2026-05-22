package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.pgrss.enums.StatusColetaInterna;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.Objects;

@Audited
@Entity
@Table(name = "pgrss_coletas_internas",
        indexes = {
                @Index(name = "ix_pgrss_ci_tenant_status", columnList = "tenant_id,status")
        })
public class ColetaInterna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "data_hora_coleta", nullable = false)
    private LocalDateTime dataHoraColeta;

    @Column(name = "nome_rota", length = 80)
    private String nomeRota;

    @Column(name = "responsavel_nome", nullable = false, length = 120)
    private String responsavelNome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusColetaInterna status = StatusColetaInterna.INICIADA;

    @Column(length = 255)
    private String observacoes;

    public ColetaInterna() {}

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public LocalDateTime getDataHoraColeta() { return dataHoraColeta; }
    public void setDataHoraColeta(LocalDateTime dataHoraColeta) { this.dataHoraColeta = dataHoraColeta; }
    public String getNomeRota() { return nomeRota; }
    public void setNomeRota(String nomeRota) { this.nomeRota = nomeRota; }
    public String getResponsavelNome() { return responsavelNome; }
    public void setResponsavelNome(String responsavelNome) { this.responsavelNome = responsavelNome; }
    public StatusColetaInterna getStatus() { return status; }
    public void setStatus(StatusColetaInterna status) { this.status = status; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public boolean equals(Object o) { return o instanceof ColetaInterna c && Objects.equals(id, c.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
