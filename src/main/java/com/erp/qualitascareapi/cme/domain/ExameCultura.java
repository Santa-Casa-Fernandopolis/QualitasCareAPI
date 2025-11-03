package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.ExameCulturaResultado;
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
@Table(name = "cme_exames_cultura")
public class ExameCultura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(length = 120)
    private String origemAmostra;

    @Column(nullable = false)
    private LocalDate dataColeta;

    @Column(length = 150)
    private String responsavelColeta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExameCulturaResultado resultado = ExameCulturaResultado.PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por_id")
    private User registradoPor;

    @Column(length = 255)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_exame_cultura_evidencias",
            joinColumns = @JoinColumn(name = "exame_id"),
            inverseJoinColumns = @JoinColumn(name = "evidencia_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    public ExameCultura() {
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

    public String getOrigemAmostra() {
        return origemAmostra;
    }

    public void setOrigemAmostra(String origemAmostra) {
        this.origemAmostra = origemAmostra;
    }

    public LocalDate getDataColeta() {
        return dataColeta;
    }

    public void setDataColeta(LocalDate dataColeta) {
        this.dataColeta = dataColeta;
    }

    public String getResponsavelColeta() {
        return responsavelColeta;
    }

    public void setResponsavelColeta(String responsavelColeta) {
        this.responsavelColeta = responsavelColeta;
    }

    public ExameCulturaResultado getResultado() {
        return resultado;
    }

    public void setResultado(ExameCulturaResultado resultado) {
        this.resultado = resultado;
    }

    public User getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(User registradoPor) {
        this.registradoPor = registradoPor;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Set<EvidenciaArquivo> getEvidencias() {
        return evidencias;
    }

    public void setEvidencias(Set<EvidenciaArquivo> evidencias) {
        this.evidencias = evidencias;
    }
}
