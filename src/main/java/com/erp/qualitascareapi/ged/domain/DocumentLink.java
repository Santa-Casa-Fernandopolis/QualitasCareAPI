package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "document_link",
        uniqueConstraints = @UniqueConstraint(name = "uq_document_link_rel",
                columnNames = {"tenant_id", "documento_origem_id", "documento_alvo_id", "relacao"}))
public class DocumentLink {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="documento_origem_id", nullable=false)
    private Document documentoOrigem;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="documento_alvo_id", nullable=false)
    private Document documentoAlvo;

    @Column(nullable=false, length=30)
    private String relacao; // RELATED | SUBSTITUI | SUBSTITUIDO_POR

    public DocumentLink() {}
    public DocumentLink(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public Document getDocumentoOrigem() { return documentoOrigem; }
    public Document getDocumentoAlvo() { return documentoAlvo; }
    public String getRelacao() { return relacao; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setDocumentoOrigem(Document documentoOrigem) { this.documentoOrigem = documentoOrigem; }
    public void setDocumentoAlvo(Document documentoAlvo) { this.documentoAlvo = documentoAlvo; }
    public void setRelacao(String relacao) { this.relacao = relacao; }
}
