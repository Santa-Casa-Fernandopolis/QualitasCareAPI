package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.hr.domain.Colaborador;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(name="edu_attendance",
        uniqueConstraints = @UniqueConstraint(name="uq_edu_attendance_session_colab", columnNames={"session_id","colaborador_id"}))
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="session_id", nullable=false)
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="colaborador_id", nullable=false)
    private Colaborador colaborador;

    @Column(nullable=false)
    private Boolean presente;

    private LocalDateTime registradoEm;

    @Column(columnDefinition = "text")
    private String observacoes;

    public Attendance() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }
    public Colaborador getColaborador() { return colaborador; }
    public void setColaborador(Colaborador colaborador) { this.colaborador = colaborador; }
    public Boolean getPresente() { return presente; }
    public void setPresente(Boolean presente) { this.presente = presente; }
    public LocalDateTime getRegistradoEm() { return registradoEm; }
    public void setRegistradoEm(LocalDateTime registradoEm) { this.registradoEm = registradoEm; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
