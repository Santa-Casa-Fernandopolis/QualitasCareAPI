package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.edu.enums.DeliveryMode;
import com.erp.qualitascareapi.edu.enums.GradeScale;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name="edu_courses",
        uniqueConstraints = @UniqueConstraint(name="uq_edu_course_codigo_tenant", columnNames={"tenant_id","codigo"}))
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="provider_id", nullable=false)
    private TrainingProvider provider;

    @Column(nullable=false, length=60)
    private String codigo;

    @Column(nullable=false, length=200)
    private String titulo;

    @Column(columnDefinition = "text")
    private String descricao;

    private Integer cargaHorariaMin;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private DeliveryMode deliveryMode;

    @Column(nullable=false)
    private Boolean obrigatorio;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private GradeScale gradeScale;

    public Course() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public TrainingProvider getProvider() { return provider; }
    public void setProvider(TrainingProvider provider) { this.provider = provider; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Integer getCargaHorariaMin() { return cargaHorariaMin; }
    public void setCargaHorariaMin(Integer cargaHorariaMin) { this.cargaHorariaMin = cargaHorariaMin; }
    public DeliveryMode getDeliveryMode() { return deliveryMode; }
    public void setDeliveryMode(DeliveryMode deliveryMode) { this.deliveryMode = deliveryMode; }
    public Boolean getObrigatorio() { return obrigatorio; }
    public void setObrigatorio(Boolean obrigatorio) { this.obrigatorio = obrigatorio; }
    public GradeScale getGradeScale() { return gradeScale; }
    public void setGradeScale(GradeScale gradeScale) { this.gradeScale = gradeScale; }
}
