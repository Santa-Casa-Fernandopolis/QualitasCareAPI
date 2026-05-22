package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.pgrss.enums.StatusColetaExterna;
import com.erp.qualitascareapi.pgrss.enums.TipoDestinoFinal;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Audited
@Entity
@Table(name = "pgrss_coletas_externas",
        indexes = {
                @Index(name = "ix_pgrss_ce_tenant_data", columnList = "tenant_id,data_coleta"),
                @Index(name = "ix_pgrss_ce_tenant_status", columnList = "tenant_id,status")
        })
public class ColetaExterna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaColetora empresa;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    private GrupoResiduo grupo;

    @Column(name = "data_coleta", nullable = false)
    private LocalDate dataColeta;

    @Column(name = "peso_total_kg", nullable = false, precision = 10, scale = 3)
    private BigDecimal pesoTotalKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoDestinoFinal destinacao;

    @Column(name = "numero_manifesto", length = 80)
    private String numeroManifesto;

    @Column(name = "numero_certificado_destinacao", length = 80)
    private String numeroCertificadoDestinacao;

    @Column(name = "placa_veiculo", length = 10)
    private String placaVeiculo;

    @Column(name = "nome_motorista", length = 120)
    private String nomeMotorista;

    @Column(name = "responsavel_nome", nullable = false, length = 120)
    private String responsavelNome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusColetaExterna status = StatusColetaExterna.REGISTRADA;

    @Column(length = 255)
    private String observacoes;

    public ColetaExterna() {}

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public EmpresaColetora getEmpresa() { return empresa; }
    public void setEmpresa(EmpresaColetora empresa) { this.empresa = empresa; }
    public GrupoResiduo getGrupo() { return grupo; }
    public void setGrupo(GrupoResiduo grupo) { this.grupo = grupo; }
    public LocalDate getDataColeta() { return dataColeta; }
    public void setDataColeta(LocalDate dataColeta) { this.dataColeta = dataColeta; }
    public BigDecimal getPesoTotalKg() { return pesoTotalKg; }
    public void setPesoTotalKg(BigDecimal pesoTotalKg) { this.pesoTotalKg = pesoTotalKg; }
    public TipoDestinoFinal getDestinacao() { return destinacao; }
    public void setDestinacao(TipoDestinoFinal destinacao) { this.destinacao = destinacao; }
    public String getNumeroManifesto() { return numeroManifesto; }
    public void setNumeroManifesto(String numeroManifesto) { this.numeroManifesto = numeroManifesto; }
    public String getNumeroCertificadoDestinacao() { return numeroCertificadoDestinacao; }
    public void setNumeroCertificadoDestinacao(String numeroCertificadoDestinacao) { this.numeroCertificadoDestinacao = numeroCertificadoDestinacao; }
    public String getPlacaVeiculo() { return placaVeiculo; }
    public void setPlacaVeiculo(String placaVeiculo) { this.placaVeiculo = placaVeiculo; }
    public String getNomeMotorista() { return nomeMotorista; }
    public void setNomeMotorista(String nomeMotorista) { this.nomeMotorista = nomeMotorista; }
    public String getResponsavelNome() { return responsavelNome; }
    public void setResponsavelNome(String responsavelNome) { this.responsavelNome = responsavelNome; }
    public StatusColetaExterna getStatus() { return status; }
    public void setStatus(StatusColetaExterna status) { this.status = status; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public boolean equals(Object o) { return o instanceof ColetaExterna c && Objects.equals(id, c.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
