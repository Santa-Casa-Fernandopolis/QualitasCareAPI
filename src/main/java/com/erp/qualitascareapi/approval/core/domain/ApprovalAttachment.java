package com.erp.qualitascareapi.approval.core.domain;

import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity @Audited
@Table(name="approval_attachments")
public class ApprovalAttachment {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="request_id", nullable=false)
    private ApprovalRequest request;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="arquivo_id", nullable=false)
    private EvidenciaArquivo arquivo;

    @Column(length=300)
    private String nota;

    public ApprovalAttachment() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApprovalRequest getRequest() { return request; }
    public void setRequest(ApprovalRequest request) { this.request = request; }
    public EvidenciaArquivo getArquivo() { return arquivo; }
    public void setArquivo(EvidenciaArquivo arquivo) { this.arquivo = arquivo; }
    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
}
