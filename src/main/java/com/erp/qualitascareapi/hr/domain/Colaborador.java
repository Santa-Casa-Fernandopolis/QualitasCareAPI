package com.erp.qualitascareapi.hr.domain;

import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.hr.enums.ColaboradorStatus;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

/**
 * Representa um colaborador (RH básico), independente do usuário do sistema.
 * Observações:
 *  - Substituímos o antigo campo "ativo" por "status" (ColaboradorStatus).
 *  - Multitenant: uniqueness por tenant para matrícula e CPF.
 *  - usuarioSistema é opcional (nem todo colaborador tem login).
 */
@Entity
@Audited
@Table(
        name = "hr_colaboradores",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_colab_tenant_matricula",
                        columnNames = {"tenant_id", "matricula"}
                ),
                @UniqueConstraint(
                        name = "uq_colab_tenant_cpf",
                        columnNames = {"tenant_id", "cpf"}
                )
        },
        indexes = {
                @Index(name = "idx_colab_tenant_status", columnList = "tenant_id,status"),
                @Index(name = "idx_colab_tenant_setor", columnList = "tenant_id,setor_id")
        }
)
public class Colaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tenant para escopo e filtros. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @NotNull
    private Tenant tenant;

    @NotBlank
    @Size(max = 40)
    @Column(name = "matricula", length = 40, nullable = false)
    private String matricula;

    @NotBlank
    @Size(max = 180)
    @Column(name = "nome_completo", length = 180, nullable = false)
    private String nomeCompleto;

    @NotBlank
    @Size(max = 14) // CPF formatado "000.000.000-00" ou apenas dígitos; ajuste se desejar
    @Column(name = "cpf", length = 14, nullable = false)
    private String cpf;

    @Email
    @Size(max = 180)
    @Column(name = "email", length = 180)
    private String email;

    @Size(max = 30)
    @Column(name = "telefone", length = 30)
    private String telefone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id", nullable = false)
    @NotNull
    private Setor setor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id", nullable = false)
    @NotNull
    private Cargo cargo;

    @PastOrPresent
    @Column(name = "data_admissao")
    private LocalDate dataAdmissao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @NotNull
    private ColaboradorStatus status = ColaboradorStatus.ATIVO;

    /** Opcional: nem todo colaborador possui usuário no sistema. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_sistema_id")
    private User usuarioSistema;

    public Colaborador() {
    }

    public Colaborador(Long id) {
        this.id = id;
    }

    /* ================= Getters/Setters ================= */

    public Long getId() {
        return id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public ColaboradorStatus getStatus() {
        return status;
    }

    public void setStatus(ColaboradorStatus status) {
        this.status = status;
    }

    public User getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(User usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }
}
