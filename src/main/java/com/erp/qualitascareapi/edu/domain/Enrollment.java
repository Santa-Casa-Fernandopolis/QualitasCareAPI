package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.edu.enums.AttemptStatus;
import com.erp.qualitascareapi.hr.domain.Colaborador;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(name="edu_enrollments",
        uniqueConstraints=@UniqueConstraint(name="uq_edu_enrollment_offering_colab", columnNames={"offering_id","colaborador_id"}))
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="offering_id", nullable=false)
    private Offering offering;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="colaborador_id", nullable=false)
    private Colaborador colaborador;

    private LocalDateTime inscritoEm;

    @Column(nullable=false)
    private Boolean obrigatorio;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private AttemptStatus statusGeral;

    private Double notaFinal;

    private LocalDateTime concluidoEm;

    public Enrollment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public Offering getOffering() { return offering; }
    public void setOffering(Offering offering) { this.offering = offering; }
    public Colaborador getColaborador() { return colaborador; }
    public void setColaborador(Colaborador colaborador) { this.colaborador = colaborador; }
    public LocalDateTime getInscritoEm() { return inscritoEm; }
    public void setInscritoEm(LocalDateTime inscritoEm) { this.inscritoEm = inscritoEm; }
    public Boolean getObrigatorio() { return obrigatorio; }
    public void setObrigatorio(Boolean obrigatorio) { this.obrigatorio = obrigatorio; }
    public AttemptStatus getStatusGeral() { return statusGeral; }
    public void setStatusGeral(AttemptStatus statusGeral) { this.statusGeral = statusGeral; }
    public Double getNotaFinal() { return notaFinal; }
    public void setNotaFinal(Double notaFinal) { this.notaFinal = notaFinal; }
    public LocalDateTime getConcluidoEm() { return concluidoEm; }
    public void setConcluidoEm(LocalDateTime concluidoEm) { this.concluidoEm = concluidoEm; }
}

