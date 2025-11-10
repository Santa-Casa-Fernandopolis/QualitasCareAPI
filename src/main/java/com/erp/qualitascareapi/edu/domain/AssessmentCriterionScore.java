package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name="edu_assessment_criterion_scores",
        uniqueConstraints = @UniqueConstraint(name="uq_edu_score_assessment_criterion", columnNames={"assessment_id","criterion_id"}))
public class AssessmentCriterionScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="assessment_id", nullable=false)
    private PracticalAssessment assessment;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="criterion_id", nullable=false)
    private RubricCriterion criterion;

    private Double pontuacao;

    @Column(columnDefinition = "text")
    private String comentario;

    public AssessmentCriterionScore() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public PracticalAssessment getAssessment() { return assessment; }
    public void setAssessment(PracticalAssessment assessment) { this.assessment = assessment; }
    public RubricCriterion getCriterion() { return criterion; }
    public void setCriterion(RubricCriterion criterion) { this.criterion = criterion; }
    public Double getPontuacao() { return pontuacao; }
    public void setPontuacao(Double pontuacao) { this.pontuacao = pontuacao; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
