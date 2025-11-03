package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.NaoConformidadeSeveridade;
import com.erp.qualitascareapi.cme.enums.NaoConformidadeStatus;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Audited
@Entity
@Table(name = "cme_nao_conformidades")
public class NaoConformidadeCME {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 255)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NaoConformidadeSeveridade severidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NaoConformidadeStatus status = NaoConformidadeStatus.ABERTA;

    @Column(nullable = false)
    private LocalDate dataAbertura;

    @Column
    private LocalDate dataEncerramento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private User responsavel;

    @Column(length = 255)
    private String planoAcaoResumo;

    @ManyToMany
    @JoinTable(name = "cme_nao_conformidade_evidencias",
            joinColumns = @JoinColumn(name = "nao_conformidade_id"),
            inverseJoinColumns = @JoinColumn(name = "evidencia_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    public NaoConformidadeCME() {
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public NaoConformidadeSeveridade getSeveridade() {
        return severidade;
    }

    public void setSeveridade(NaoConformidadeSeveridade severidade) {
        this.severidade = severidade;
    }

    public NaoConformidadeStatus getStatus() {
        return status;
    }

    public void setStatus(NaoConformidadeStatus status) {
        this.status = status;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDate getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(LocalDate dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    public User getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(User responsavel) {
        this.responsavel = responsavel;
    }

    public String getPlanoAcaoResumo() {
        return planoAcaoResumo;
    }

    public void setPlanoAcaoResumo(String planoAcaoResumo) {
        this.planoAcaoResumo = planoAcaoResumo;
    }

    public Set<EvidenciaArquivo> getEvidencias() {
        return evidencias;
    }

    public void setEvidencias(Set<EvidenciaArquivo> evidencias) {
        this.evidencias = evidencias;
    }
}
