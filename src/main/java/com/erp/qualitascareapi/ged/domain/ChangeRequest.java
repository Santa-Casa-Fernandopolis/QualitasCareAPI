package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.ged.enums.ChangeRequestStatus;
import com.erp.qualitascareapi.ged.enums.ImpactLevel;
import com.erp.qualitascareapi.ged.enums.Priority;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Audited
@Table(name = "change_request")
public class ChangeRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="documento_id", nullable=false)
    private Document documento;

    @Column(nullable=false, length=200)
    private String titulo;

    @Column(length=1000)
    private String motivo;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="solicitante_id", nullable=false)
    private User solicitante;

    private LocalDateTime solicitadoEm;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=10)
    private Priority prioridade;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=10)
    private ImpactLevel impacto;

    private LocalDate prazo;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="responsavel_id")
    private User responsavel;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private ChangeRequestStatus status;

    private LocalDateTime slaEstouradoEm;

    /** opcional: versão que atendeu a CR */
    @Column(name="implemented_by_version_id")
    private Long implementedByVersionId;

    public ChangeRequest() {}
    public ChangeRequest(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public Document getDocumento() { return documento; }
    public String getTitulo() { return titulo; }
    public String getMotivo() { return motivo; }
    public User getSolicitante() { return solicitante; }
    public LocalDateTime getSolicitadoEm() { return solicitadoEm; }
    public Priority getPrioridade() { return prioridade; }
    public ImpactLevel getImpacto() { return impacto; }
    public LocalDate getPrazo() { return prazo; }
    public User getResponsavel() { return responsavel; }
    public ChangeRequestStatus getStatus() { return status; }
    public LocalDateTime getSlaEstouradoEm() { return slaEstouradoEm; }
    public Long getImplementedByVersionId() { return implementedByVersionId; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setDocumento(Document documento) { this.documento = documento; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public void setSolicitante(User solicitante) { this.solicitante = solicitante; }
    public void setSolicitadoEm(LocalDateTime solicitadoEm) { this.solicitadoEm = solicitadoEm; }
    public void setPrioridade(Priority prioridade) { this.prioridade = prioridade; }
    public void setImpacto(ImpactLevel impacto) { this.impacto = impacto; }
    public void setPrazo(LocalDate prazo) { this.prazo = prazo; }
    public void setResponsavel(User responsavel) { this.responsavel = responsavel; }
    public void setStatus(ChangeRequestStatus status) { this.status = status; }
    public void setSlaEstouradoEm(LocalDateTime slaEstouradoEm) { this.slaEstouradoEm = slaEstouradoEm; }
    public void setImplementedByVersionId(Long implementedByVersionId) { this.implementedByVersionId = implementedByVersionId; }
}
