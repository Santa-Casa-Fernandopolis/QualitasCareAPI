// =====================================
// = ENTIDADES (com.erp.qualitascareapi.cme.domain)
// =====================================
package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.*;
import com.erp.qualitascareapi.approval.core.contracts.ApprovableTarget;
import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.core.domain.KitVersion;
import com.erp.qualitascareapi.iam.domain.Setor;
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

/** ---------------- Autoclave ---------------- */
@Audited
@Entity
@Table(name = "cme_autoclaves",
        uniqueConstraints = @UniqueConstraint(name = "uk_autoclave_tenant_numero_serie",
                columnNames = {"tenant_id", "numero_serie"}),
        indexes = {
                @Index(name = "ix_autoclave_tenant_nome", columnList = "tenant_id,nome")
        })
public class Autoclave {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotBlank @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank @Column(nullable = false, length = 120)
    private String fabricante;

    @NotBlank @Column(nullable = false, length = 120)
    private String modelo;

    @NotBlank @Column(name = "numero_serie", nullable = false, length = 120)
    private String numeroSerie;

    @Column(length = 180)
    private String localizacao;

    private LocalDate ultimaHigienizacaoProfunda;

    @NotNull
    @Column(nullable = false)
    private Boolean ativo;

    // getters/setters/equals/hashCode
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public LocalDate getUltimaHigienizacaoProfunda() { return ultimaHigienizacaoProfunda; }
    public void setUltimaHigienizacaoProfunda(LocalDate d) { this.ultimaHigienizacaoProfunda = d; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    @Override public boolean equals(Object o){ return o instanceof Autoclave a && Objects.equals(id, a.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
