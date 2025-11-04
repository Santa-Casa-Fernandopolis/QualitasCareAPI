package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Entity
@Audited
@Table(name = "communication_profile")
public class CommunicationProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="documento_id", nullable=false)
    private Document documento;

    @Column(length=60)
    private String tipoMensagem; // Alerta, Nota técnica, Circular

    @Column(length=500)
    private String publicoAlvo; // setores/roles

    @Column(nullable=false)
    private Boolean ackObrigatorio = Boolean.FALSE;

    private LocalDate validoDe;
    private LocalDate validoAte;

    public CommunicationProfile() {}
    public CommunicationProfile(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public Document getDocumento() { return documento; }
    public String getTipoMensagem() { return tipoMensagem; }
    public String getPublicoAlvo() { return publicoAlvo; }
    public Boolean getAckObrigatorio() { return ackObrigatorio; }
    public LocalDate getValidoDe() { return validoDe; }
    public LocalDate getValidoAte() { return validoAte; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setDocumento(Document documento) { this.documento = documento; }
    public void setTipoMensagem(String tipoMensagem) { this.tipoMensagem = tipoMensagem; }
    public void setPublicoAlvo(String publicoAlvo) { this.publicoAlvo = publicoAlvo; }
    public void setAckObrigatorio(Boolean ackObrigatorio) { this.ackObrigatorio = ackObrigatorio; }
    public void setValidoDe(LocalDate validoDe) { this.validoDe = validoDe; }
    public void setValidoAte(LocalDate validoAte) { this.validoAte = validoAte; }
}
