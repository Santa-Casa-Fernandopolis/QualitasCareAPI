package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.LoteStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "vw_cme_kit_fisico_lotes")
public class CmeKitFisicoLoteView {

    @Id
    @Column(name = "lote_id")
    private Long id;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "kit_fisico_id")
    private Long kitFisicoId;

    @Column(name = "kit_fisico_identificador")
    private String kitFisicoIdentificador;

    @Column(name = "processo_id")
    private Long processoId;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "kit_versao_id")
    private Long kitVersaoId;

    @Column(name = "data_empacotamento")
    private LocalDate dataEmpacotamento;

    @Column(name = "validade")
    private LocalDate validade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LoteStatus status;

    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "montado_por_id")
    private Long montadoPorId;

    @Column(name = "data_hora_inicio_montagem")
    private LocalDateTime dataHoraInicioMontagem;

    @Column(name = "data_hora_fim_montagem")
    private LocalDateTime dataHoraFimMontagem;

    @Column(name = "observacoes")
    private String observacoes;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getKitFisicoId() { return kitFisicoId; }
    public String getKitFisicoIdentificador() { return kitFisicoIdentificador; }
    public Long getProcessoId() { return processoId; }
    public String getCodigo() { return codigo; }
    public Long getKitVersaoId() { return kitVersaoId; }
    public LocalDate getDataEmpacotamento() { return dataEmpacotamento; }
    public LocalDate getValidade() { return validade; }
    public LoteStatus getStatus() { return status; }
    public String getQrCode() { return qrCode; }
    public Long getMontadoPorId() { return montadoPorId; }
    public LocalDateTime getDataHoraInicioMontagem() { return dataHoraInicioMontagem; }
    public LocalDateTime getDataHoraFimMontagem() { return dataHoraFimMontagem; }
    public String getObservacoes() { return observacoes; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
