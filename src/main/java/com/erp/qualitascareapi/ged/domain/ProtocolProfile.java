package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "protocol_profile")
public class ProtocolProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="documento_id", nullable=false)
    private Document documento;

    @Column(length=300)
    private String escopoClinico;

    @Lob private String indicacoes;
    @Lob private String contraIndicacoes;

    @Column(length=200)
    private String fluxogramaRef;

    public ProtocolProfile() {}
    public ProtocolProfile(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public Document getDocumento() { return documento; }
    public String getEscopoClinico() { return escopoClinico; }
    public String getIndicacoes() { return indicacoes; }
    public String getContraIndicacoes() { return contraIndicacoes; }
    public String getFluxogramaRef() { return fluxogramaRef; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setDocumento(Document documento) { this.documento = documento; }
    public void setEscopoClinico(String escopoClinico) { this.escopoClinico = escopoClinico; }
    public void setIndicacoes(String indicacoes) { this.indicacoes = indicacoes; }
    public void setContraIndicacoes(String contraIndicacoes) { this.contraIndicacoes = contraIndicacoes; }
    public void setFluxogramaRef(String fluxogramaRef) { this.fluxogramaRef = fluxogramaRef; }
}
