package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "distribution_rule")
public class DistributionRule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="documento_id", nullable=false)
    private Document documento;

    @Column(length=500)
    private String departamentosAlvo; // separados por ";"

    @Column(length=500)
    private String papeisAlvo; // separados por ";"

    public DistributionRule() {}
    public DistributionRule(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public Document getDocumento() { return documento; }
    public String getDepartamentosAlvo() { return departamentosAlvo; }
    public String getPapeisAlvo() { return papeisAlvo; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setDocumento(Document documento) { this.documento = documento; }
    public void setDepartamentosAlvo(String departamentosAlvo) { this.departamentosAlvo = departamentosAlvo; }
    public void setPapeisAlvo(String papeisAlvo) { this.papeisAlvo = papeisAlvo; }
}
