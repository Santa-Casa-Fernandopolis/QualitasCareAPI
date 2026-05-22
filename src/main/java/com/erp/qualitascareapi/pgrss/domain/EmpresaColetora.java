package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.Objects;

@Audited
@Entity
@Table(name = "pgrss_empresas_coletoras",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_pgrss_ec_tenant_cnpj", columnNames = {"tenant_id", "cnpj"})
        },
        indexes = {
                @Index(name = "ix_pgrss_ec_tenant_vencimento", columnList = "tenant_id,data_vencimento_licenca")
        })
public class EmpresaColetora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "razao_social", nullable = false, length = 200)
    private String razaoSocial;

    @Column(name = "nome_fantasia", length = 120)
    private String nomeFantasia;

    @Column(nullable = false, length = 18)
    private String cnpj;

    @Column(name = "numero_licenca", nullable = false, length = 80)
    private String numeroLicenca;

    @Column(name = "data_vencimento_licenca", nullable = false)
    private LocalDate dataVencimentoLicenca;

    @Column(name = "nome_contato", length = 120)
    private String nomeContato;

    @Column(length = 20)
    private String telefone;

    @Column(length = 120)
    private String email;

    @Column(nullable = false)
    private boolean ativo = true;

    public EmpresaColetora() {}

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getNumeroLicenca() { return numeroLicenca; }
    public void setNumeroLicenca(String numeroLicenca) { this.numeroLicenca = numeroLicenca; }
    public LocalDate getDataVencimentoLicenca() { return dataVencimentoLicenca; }
    public void setDataVencimentoLicenca(LocalDate dataVencimentoLicenca) { this.dataVencimentoLicenca = dataVencimentoLicenca; }
    public String getNomeContato() { return nomeContato; }
    public void setNomeContato(String nomeContato) { this.nomeContato = nomeContato; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public boolean equals(Object o) { return o instanceof EmpresaColetora e && Objects.equals(id, e.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
