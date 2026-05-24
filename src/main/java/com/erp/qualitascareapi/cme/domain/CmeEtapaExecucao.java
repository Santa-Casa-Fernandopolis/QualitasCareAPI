package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.CmeEtapaExecucaoStatus;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.Objects;

@Audited
@Entity
@Table(name = "cme_etapas_execucao",
        uniqueConstraints = @UniqueConstraint(name = "uk_cme_exec_processo_etapa",
                columnNames = {"processo_id", "etapa_id"}),
        indexes = {
                @Index(name = "ix_cme_exec_processo_status", columnList = "processo_id,status"),
                @Index(name = "ix_cme_exec_etapa_status", columnList = "etapa_id,status")
        })
public class CmeEtapaExecucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "processo_id", nullable = false)
    private ProcessoReprocessamento processo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "etapa_id", nullable = false)
    private CmeEtapaProcesso etapa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CmeEtapaExecucaoStatus status = CmeEtapaExecucaoStatus.PENDENTE;

    @Column(name = "data_hora_inicio")
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim")
    private LocalDateTime dataHoraFim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private User responsavel;

    @Column(length = 255)
    private String justificativa;

    @Column(length = 255)
    private String observacoes;

    public Long getId() { return id; }
    public ProcessoReprocessamento getProcesso() { return processo; }
    public void setProcesso(ProcessoReprocessamento processo) { this.processo = processo; }
    public CmeEtapaProcesso getEtapa() { return etapa; }
    public void setEtapa(CmeEtapaProcesso etapa) { this.etapa = etapa; }
    public CmeEtapaExecucaoStatus getStatus() { return status; }
    public void setStatus(CmeEtapaExecucaoStatus status) { this.status = status; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }
    public LocalDateTime getDataHoraFim() { return dataHoraFim; }
    public void setDataHoraFim(LocalDateTime dataHoraFim) { this.dataHoraFim = dataHoraFim; }
    public User getResponsavel() { return responsavel; }
    public void setResponsavel(User responsavel) { this.responsavel = responsavel; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public boolean equals(Object o) { return o instanceof CmeEtapaExecucao e && Objects.equals(id, e.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
