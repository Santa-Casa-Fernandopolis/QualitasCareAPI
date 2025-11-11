package com.erp.qualitascareapi.quality.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(name = "nc_eficacia_avaliacao")
public class NCEficaciaAvaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "nc_id", nullable = false)
    private NaoConformidadeCME nc;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "avaliador_id", nullable = false)
    private User avaliador;

    @Column(name = "avaliado_em", nullable = false)
    private LocalDateTime avaliadoEm;

    @Column(length = 200)
    private String metodo;

    @Column(length = 1000)
    private String resultado;

    @Column(nullable = false)
    private Boolean eficaz;

    public NCEficaciaAvaliacao() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public NaoConformidadeCME getNc() { return nc; }
    public void setNc(NaoConformidadeCME nc) { this.nc = nc; }

    public User getAvaliador() { return avaliador; }
    public void setAvaliador(User avaliador) { this.avaliador = avaliador; }

    public LocalDateTime getAvaliadoEm() { return avaliadoEm; }
    public void setAvaliadoEm(LocalDateTime avaliadoEm) { this.avaliadoEm = avaliadoEm; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public Boolean getEficaz() { return eficaz; }
    public void setEficaz(Boolean eficaz) { this.eficaz = eficaz; }
}
