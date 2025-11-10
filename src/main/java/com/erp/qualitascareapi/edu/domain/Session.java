package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(name="edu_sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="offering_id", nullable=false)
    private Offering offering;

    private LocalDateTime inicio;
    private LocalDateTime fim;

    @Column(length=200)
    private String local;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="instrutor_id", nullable=false)
    private CourseInstructor instrutor;

    public Session() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public Offering getOffering() { return offering; }
    public void setOffering(Offering offering) { this.offering = offering; }
    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }
    public LocalDateTime getFim() { return fim; }
    public void setFim(LocalDateTime fim) { this.fim = fim; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public CourseInstructor getInstrutor() { return instrutor; }
    public void setInstrutor(CourseInstructor instrutor) { this.instrutor = instrutor; }
}
