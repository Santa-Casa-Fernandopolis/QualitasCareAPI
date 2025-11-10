package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.edu.enums.BookingStatus;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(name="edu_resource_bookings")
public class ResourceBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="resource_id", nullable=false)
    private TrainingResource resource;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="session_id", nullable=false)
    private Session session;

    private LocalDateTime inicio;
    private LocalDateTime fim;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private BookingStatus status;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="solicitado_por_id", nullable=false)
    private User solicitadoPor;

    @Column(columnDefinition = "text")
    private String observacoes;

    public ResourceBooking() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public TrainingResource getResource() { return resource; }
    public void setResource(TrainingResource resource) { this.resource = resource; }
    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }
    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }
    public LocalDateTime getFim() { return fim; }
    public void setFim(LocalDateTime fim) { this.fim = fim; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public User getSolicitadoPor() { return solicitadoPor; }
    public void setSolicitadoPor(User solicitadoPor) { this.solicitadoPor = solicitadoPor; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
