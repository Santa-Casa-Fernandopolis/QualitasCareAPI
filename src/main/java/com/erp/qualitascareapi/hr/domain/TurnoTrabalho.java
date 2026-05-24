package com.erp.qualitascareapi.hr.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalTime;

@Entity
@Audited
@Table(name = "hr_turnos_trabalho",
        uniqueConstraints = @UniqueConstraint(name = "uq_hr_turno_codigo_tenant", columnNames = {"tenant_id", "codigo"}))
public class TurnoTrabalho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 60)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    @Column(name = "cruza_meia_noite", nullable = false)
    private boolean cruzaMeiaNoite = false;

    @Column(name = "intervalo_minutos")
    private Integer intervaloMinutos = 0;

    @Column(nullable = false)
    private boolean active = true;

    @Column(columnDefinition = "text")
    private String descricao;

    public TurnoTrabalho() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    public boolean isCruzaMeiaNoite() { return cruzaMeiaNoite; }
    public void setCruzaMeiaNoite(boolean cruzaMeiaNoite) { this.cruzaMeiaNoite = cruzaMeiaNoite; }
    public Integer getIntervaloMinutos() { return intervaloMinutos; }
    public void setIntervaloMinutos(Integer intervaloMinutos) { this.intervaloMinutos = intervaloMinutos; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
