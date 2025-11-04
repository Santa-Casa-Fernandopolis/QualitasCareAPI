package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "doc_tag",
        uniqueConstraints = @UniqueConstraint(name = "uq_doc_tag_tenant_nome",
                columnNames = {"tenant_id", "nome"}))
public class DocTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 80)
    private String nome;

    public DocTag() {}
    public DocTag(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public String getNome() { return nome; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setNome(String nome) { this.nome = nome; }
}
