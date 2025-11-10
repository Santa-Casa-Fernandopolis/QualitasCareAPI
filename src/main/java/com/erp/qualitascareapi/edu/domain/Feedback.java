package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.edu.enums.FeedbackTarget;
import com.erp.qualitascareapi.hr.domain.Colaborador;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(name="edu_feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private FeedbackTarget targetType;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="session_id")
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="instructor_id")
    private CourseInstructor instructor;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="colaborador_id", nullable=false)
    private Colaborador colaborador;

    @Column(nullable=false)
    private Integer nota;

    @Column(columnDefinition = "text")
    private String comentario;

    private LocalDateTime enviadoEm;

    public Feedback() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public FeedbackTarget getTargetType() { return targetType; }
    public void setTargetType(FeedbackTarget targetType) { this.targetType = targetType; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }
    public CourseInstructor getInstructor() { return instructor; }
    public void setInstructor(CourseInstructor instructor) { this.instructor = instructor; }
    public Colaborador getColaborador() { return colaborador; }
    public void setColaborador(Colaborador colaborador) { this.colaborador = colaborador; }
    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getEnviadoEm() { return enviadoEm; }
    public void setEnviadoEm(LocalDateTime enviadoEm) { this.enviadoEm = enviadoEm; }
}
