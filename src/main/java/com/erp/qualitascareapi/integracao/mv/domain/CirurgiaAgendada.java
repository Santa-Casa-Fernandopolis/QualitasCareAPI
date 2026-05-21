package com.erp.qualitascareapi.integracao.mv.domain;

import com.erp.qualitascareapi.integracao.mv.enums.StatusCirurgiaMv;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Espelho local de uma cirurgia agendada importada do Soul MV.
 *
 * <p>Os dados são sincronizados periodicamente pelo {@code MvIntegracaoService}
 * e podem ser vinculados a saídas de kit do CME para rastreabilidade completa:
 * <b>cirurgia → kit → lote de esterilização → paciente</b>.</p>
 *
 * <p>A coluna {@code id_mv} é o identificador primário no sistema MV e garante
 * unicidade por tenant via índice composto.</p>
 */
@Entity
@Table(name = "cme_cirurgias_agendadas",
        indexes = {
                @Index(name = "uq_cir_tenant_idmv",         columnList = "tenant_id,id_mv", unique = true),
                @Index(name = "ix_cir_tenant_data",          columnList = "tenant_id,data_hora_inicio"),
                @Index(name = "ix_cir_tenant_status",        columnList = "tenant_id,status_mv"),
                @Index(name = "ix_cir_tenant_sala",          columnList = "tenant_id,sala_cirurgica")
        })
public class CirurgiaAgendada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    /** Identificador único no sistema MV (PK no MV). */
    @Column(name = "id_mv", nullable = false, length = 40)
    private String idMv;

    @Column(name = "codigo_paciente", length = 40)
    private String codigoPaciente;

    @Column(name = "nome_paciente", length = 150)
    private String nomePaciente;

    @Column(name = "data_hora_inicio")
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim_prevista")
    private LocalDateTime dataHoraFimPrevista;

    @Column(name = "tipo_cirurgia", length = 150)
    private String tipoCirurgia;

    @Column(name = "sala_cirurgica", length = 80)
    private String salaCirurgica;

    @Column(name = "nome_cirurgiao", length = 150)
    private String nomeCirurgiao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_mv", nullable = false, length = 20)
    private StatusCirurgiaMv statusMv;

    /** Data/hora da última sincronização desta cirurgia com o MV. */
    @Column(name = "ultima_sincronizacao", nullable = false)
    private LocalDateTime ultimaSincronizacao;

    // ─── Getters / Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getIdMv() { return idMv; }
    public void setIdMv(String idMv) { this.idMv = idMv; }

    public String getCodigoPaciente() { return codigoPaciente; }
    public void setCodigoPaciente(String codigoPaciente) { this.codigoPaciente = codigoPaciente; }

    public String getNomePaciente() { return nomePaciente; }
    public void setNomePaciente(String nomePaciente) { this.nomePaciente = nomePaciente; }

    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }

    public LocalDateTime getDataHoraFimPrevista() { return dataHoraFimPrevista; }
    public void setDataHoraFimPrevista(LocalDateTime dataHoraFimPrevista) { this.dataHoraFimPrevista = dataHoraFimPrevista; }

    public String getTipoCirurgia() { return tipoCirurgia; }
    public void setTipoCirurgia(String tipoCirurgia) { this.tipoCirurgia = tipoCirurgia; }

    public String getSalaCirurgica() { return salaCirurgica; }
    public void setSalaCirurgica(String salaCirurgica) { this.salaCirurgica = salaCirurgica; }

    public String getNomeCirurgiao() { return nomeCirurgiao; }
    public void setNomeCirurgiao(String nomeCirurgiao) { this.nomeCirurgiao = nomeCirurgiao; }

    public StatusCirurgiaMv getStatusMv() { return statusMv; }
    public void setStatusMv(StatusCirurgiaMv statusMv) { this.statusMv = statusMv; }

    public LocalDateTime getUltimaSincronizacao() { return ultimaSincronizacao; }
    public void setUltimaSincronizacao(LocalDateTime ultimaSincronizacao) { this.ultimaSincronizacao = ultimaSincronizacao; }
}
