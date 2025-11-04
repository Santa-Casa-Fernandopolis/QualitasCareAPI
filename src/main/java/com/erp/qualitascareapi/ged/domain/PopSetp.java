package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "pop_step",
        indexes = @Index(name = "idx_pop_step_order", columnList = "pop_profile_id, ordem"))
public class PopStep {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="pop_profile_id", nullable=false)
    private PopProfile popProfile;

    @Column(nullable=false)
    private Integer ordem;

    @Column(length=120)
    private String titulo;

    @Lob
    private String instrucao;

    @Column(length=200)
    private String notaRisco;

    public PopStep() {}
    public PopStep(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public PopProfile getPopProfile() { return popProfile; }
    public Integer getOrdem() { return ordem; }
    public String getTitulo() { return titulo; }
    public String getInstrucao() { return instrucao; }
    public String getNotaRisco() { return notaRisco; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setPopProfile(PopProfile popProfile) { this.popProfile = popProfile; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setInstrucao(String instrucao) { this.instrucao = instrucao; }
    public void setNotaRisco(String notaRisco) { this.notaRisco = notaRisco; }
}
