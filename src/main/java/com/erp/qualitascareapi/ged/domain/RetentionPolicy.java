package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "retention_policy",
        uniqueConstraints = @UniqueConstraint(name = "uq_retention_policy_tenant_nome",
                columnNames = {"tenant_id", "nome"}))
public class RetentionPolicy {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(name = "anos_guarda", nullable = false)
    private Integer anosGuarda;

    @Column(name = "metodo_descarte", length = 200)
    private String metodoDescarte;

    @Column(name = "base_legal", length = 300)
    private String baseLegal;

    public RetentionPolicy() {}
    public RetentionPolicy(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public String getNome() { return nome; }
    public Integer getAnosGuarda() { return anosGuarda; }
    public String getMetodoDescarte() { return metodoDescarte; }
    public String getBaseLegal() { return baseLegal; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setNome(String nome) { this.nome = nome; }
    public void setAnosGuarda(Integer anosGuarda) { this.anosGuarda = anosGuarda; }
    public void setMetodoDescarte(String metodoDescarte) { this.metodoDescarte = metodoDescarte; }
    public void setBaseLegal(String baseLegal) { this.baseLegal = baseLegal; }
}
