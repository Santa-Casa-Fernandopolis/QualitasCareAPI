package com.erp.qualitascareapi.ged.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Entity
@Audited
@Table(name = "review_cycle")
public class ReviewCycle {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="documento_id", nullable=false)
    private Document documento;

    @Column(name="frequencia_meses", nullable=false)
    private Integer frequenciaMeses;

    private LocalDate dataUltimaRevisao;
    private LocalDate dataProximaRevisao;

    @Column(length=120)
    private String grupoRevisor;

    public ReviewCycle() {}
    public ReviewCycle(Long id) { this.id = id; }

    // getters/setters
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public Document getDocumento() { return documento; }
    public Integer getFrequenciaMeses() { return frequenciaMeses; }
    public LocalDate getDataUltimaRevisao() { return dataUltimaRevisao; }
    public LocalDate getDataProximaRevisao() { return dataProximaRevisao; }
    public String getGrupoRevisor() { return grupoRevisor; }
    public void setId(Long id) { this.id = id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public void setDocumento(Document documento) { this.documento = documento; }
    public void setFrequenciaMeses(Integer frequenciaMeses) { this.frequenciaMeses = frequenciaMeses; }
    public void setDataUltimaRevisao(LocalDate dataUltimaRevisao) { this.dataUltimaRevisao = dataUltimaRevisao; }
    public void setDataProximaRevisao(LocalDate dataProximaRevisao) { this.dataProximaRevisao = dataProximaRevisao; }
    public void setGrupoRevisor(String grupoRevisor) { this.grupoRevisor = grupoRevisor; }
}
