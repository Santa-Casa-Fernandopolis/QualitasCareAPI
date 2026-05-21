package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.pgrss.enums.EmpresaColetoraTipo;
import com.erp.qualitascareapi.pgrss.enums.LicencaAmbientalStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Empresa responsável pela coleta, transporte e/ou tratamento dos resíduos de saúde.
 * Deve possuir licença ambiental válida conforme RDC ANVISA nº 222/2018.
 */
@Audited
@Entity
@Table(name = "pgrss_empresas_coletoras",
        uniqueConstraints = @UniqueConstraint(name = "uk_pgrss_ec_tenant_cnpj",
                columnNames = {"tenant_id", "cnpj"}),
        indexes = {
                @Index(name = "ix_pgrss_ec_tenant_ativo", columnList = "tenant_id,ativo"),
                @Index(name = "ix_pgrss_ec_licenca_status", columnList = "tenant_id,licenca_status")
        })
public class EmpresaColetora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String razaoSocial;

    @Column(length = 18)
    private String cnpj;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EmpresaColetoraTipo tipo;

    @Column(length = 120)
    private String licencaNumero;

    private LocalDate licencaVencimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "licenca_status", length = 30)
    private LicencaAmbientalStatus licencaStatus;

    @Column(length = 30)
    private String telefone;

    @Column(length = 150)
    private String email;

    @Column(length = 150)
    private String responsavelNome;

    @Column(length = 500)
    private String observacoes;

    @Column(nullable = false)
    private Boolean ativo = Boolean.TRUE;

    public EmpresaColetora() {}

    public Long getId() { return id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public EmpresaColetoraTipo getTipo() { return tipo; }
    public void setTipo(EmpresaColetoraTipo tipo) { this.tipo = tipo; }

    public String getLicencaNumero() { return licencaNumero; }
    public void setLicencaNumero(String licencaNumero) { this.licencaNumero = licencaNumero; }

    public LocalDate getLicencaVencimento() { return licencaVencimento; }
    public void setLicencaVencimento(LocalDate licencaVencimento) { this.licencaVencimento = licencaVencimento; }

    public LicencaAmbientalStatus getLicencaStatus() { return licencaStatus; }
    public void setLicencaStatus(LicencaAmbientalStatus licencaStatus) { this.licencaStatus = licencaStatus; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getResponsavelNome() { return responsavelNome; }
    public void setResponsavelNome(String responsavelNome) { this.responsavelNome = responsavelNome; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    @Override
    public boolean equals(Object o) { return o instanceof EmpresaColetora e && Objects.equals(id, e.id); }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
