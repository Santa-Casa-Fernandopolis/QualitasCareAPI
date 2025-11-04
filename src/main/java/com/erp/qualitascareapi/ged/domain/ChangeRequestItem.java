package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "change_request_item")
public class ChangeRequestItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="change_request_id", nullable=false)
    private ChangeRequest changeRequest;

    @Column(length=120)
    private String secaoAlvo;

    @Lob
    @Column(name="texto_antes")
    private String textoAntes;

    @Lob
    @Column(name="texto_depois")
    private String textoDepois;

    @Column(length=500)
    private String justificativa;

    public ChangeRequestItem() {}
    public ChangeRequestItem(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public ChangeRequest getChangeRequest() { return changeRequest; }
    public String getSecaoAlvo() { return secaoAlvo; }
    public String getTextoAntes() { return textoAntes; }
    public String getTextoDepois() { return textoDepois; }
    public String getJustificativa() { return justificativa; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setChangeRequest(ChangeRequest changeRequest) { this.changeRequest = changeRequest; }
    public void setSecaoAlvo(String secaoAlvo) { this.secaoAlvo = secaoAlvo; }
    public void setTextoAntes(String textoAntes) { this.textoAntes = textoAntes; }
    public void setTextoDepois(String textoDepois) { this.textoDepois = textoDepois; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
}
