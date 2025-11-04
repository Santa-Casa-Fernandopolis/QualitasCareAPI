package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "pop_profile")
public class PopProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="documento_id", nullable=false)
    private Document documento;

    @Column(length=120)
    private String area;

    @Column(length=300)
    private String objetivo;

    @Column(length=300)
    private String escopo;

    @Lob private String definicoes;
    @Lob private String responsabilidades;
    @Lob private String materiais;
    @Lob private String precaucoesSeguranca;
    @Lob private String registrosGerados;
    @Lob private String referencias;

    public PopProfile() {}
    public PopProfile(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public Document getDocumento() { return documento; }
    public String getArea() { return area; }
    public String getObjetivo() { return objetivo; }
    public String getEscopo() { return escopo; }
    public String getDefinicoes() { return definicoes; }
    public String getResponsabilidades() { return responsabilidades; }
    public String getMateriais() { return materiais; }
    public String getPrecaucoesSeguranca() { return precaucoesSeguranca; }
    public String getRegistrosGerados() { return registrosGerados; }
    public String getReferencias() { return referencias; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setDocumento(Document documento) { this.documento = documento; }
    public void setArea(String area) { this.area = area; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public void setEscopo(String escopo) { this.escopo = escopo; }
    public void setDefinicoes(String definicoes) { this.definicoes = definicoes; }
    public void setResponsabilidades(String responsabilidades) { this.responsabilidades = responsabilidades; }
    public void setMateriais(String materiais) { this.materiais = materiais; }
    public void setPrecaucoesSeguranca(String precaucoesSeguranca) { this.precaucoesSeguranca = precaucoesSeguranca; }
    public void setRegistrosGerados(String registrosGerados) { this.registrosGerados = registrosGerados; }
    public void setReferencias(String referencias) { this.referencias = referencias; }
}
