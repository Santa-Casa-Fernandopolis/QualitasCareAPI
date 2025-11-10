package com.erp.qualitascareapi.edu.domain;

import com.erp.qualitascareapi.edu.enums.AttemptStatus;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(name="edu_attempts")
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="enrollment_id", nullable=false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="course_item_id", nullable=false)
    private CourseItem courseItem;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private AttemptStatus status;

    private Double scoreRaw;
    private Double progressoPct;
    private Integer tentativaN;

    public Attempt() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public Enrollment getEnrollment() { return enrollment; }
    public void setEnrollment(Enrollment enrollment) { this.enrollment = enrollment; }
    public CourseItem getCourseItem() { return courseItem; }
    public void setCourseItem(CourseItem courseItem) { this.courseItem = courseItem; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public AttemptStatus getStatus() { return status; }
    public void setStatus(AttemptStatus status) { this.status = status; }
    public Double getScoreRaw() { return scoreRaw; }
    public void setScoreRaw(Double scoreRaw) { this.scoreRaw = scoreRaw; }
    public Double getProgressoPct() { return progressoPct; }
    public void setProgressoPct(Double progressoPct) { this.progressoPct = progressoPct; }
    public Integer getTentativaN() { return tentativaN; }
    public void setTentativaN(Integer tentativaN) { this.tentativaN = tentativaN; }
}
