package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.LoteStatus;
import com.erp.qualitascareapi.cme.enums.TipoFluxoCME;
import com.erp.qualitascareapi.approval.core.contracts.ApprovableTarget;
import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.core.domain.KitVersion;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um pacote/kit embalado individualmente com seu QR code.
 * - Cirúrgico: associado a um KitVersion e a um CicloEsterilizacao.
 * - Inalatório: kitVersao pode ser nulo; aprovação ocorre no nível do lote.
 */
@Audited
@Entity
@Table(name = "cme_lotes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_lote_tenant_codigo",  columnNames = {"tenant_id", "codigo"}),
                @UniqueConstraint(name = "uk_lote_tenant_qrcode",  columnNames = {"tenant_id", "qr_code"})
        },
        indexes = {
                @Index(name = "ix_lote_tenant_status",      columnList = "tenant_id,status"),
                @Index(name = "ix_lote_tenant_tipo_fluxo",  columnList = "tenant_id,tipo_fluxo"),
                @Index(name = "ix_lote_tenant_ciclo",       columnList = "tenant_id,ciclo_esterilizacao_id"),
                @Index(name = "ix_lote_tenant_kit_versao",  columnList = "tenant_id,kit_versao_id")
        })
public class LoteEtiqueta implements ApprovableTarget {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotBlank @Column(nullable = false, length = 60)
    private String codigo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_fluxo", nullable = false, length = 20)
    private TipoFluxoCME tipoFluxo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processo_id")
    private ProcessoReprocessamento processo;

    // Nulo para materiais inalatórios
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_versao_id")
    private KitVersion kitVersao;

    // Descrição livre para inalatórios (máscara, tubo, etc.)
    @Column(name = "descricao_item", length = 200)
    private String descricaoItem;

    // Nulo para inalatórios (não passam por autoclave)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ciclo_esterilizacao_id")
    private CicloEsterilizacao cicloEsterilizacao;

    @Column(name = "data_hora_inicio_montagem")
    private LocalDateTime dataHoraInicioMontagem;

    @Column(name = "data_hora_fim_montagem")
    private LocalDateTime dataHoraFimMontagem;

    @NotNull @Column(nullable = false)
    private LocalDate dataEmpacotamento;

    @NotNull @Column(nullable = false)
    private LocalDate validade;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private LoteStatus status = LoteStatus.MONTADO;

    @NotBlank @Column(name = "qr_code", nullable = false, length = 120)
    private String qrCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "montado_por_id")
    private User montadoPor;

    @Column(length = 800)
    private String observacoes;

    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    // ApprovableTarget — ativado apenas para inalatórios (cirúrgico aprova via CicloEsterilizacao)
    @Override public Tenant getTenant()               { return tenant; }
    @Override public ApprovalDomain getApprovalDomain() { return ApprovalDomain.LOTE_INHALATORIO; }
    @Override public String getApprovalKey()          { return id == null ? null : "loteInhalatorio:" + id; }
    @Override public Setor getScopeSetor()            { return null; }

    public Long getId()                                        { return id; }
    public void setTenant(Tenant tenant)                       { this.tenant = tenant; }
    public String getCodigo()                                  { return codigo; }
    public void setCodigo(String codigo)                       { this.codigo = codigo; }
    public TipoFluxoCME getTipoFluxo()                         { return tipoFluxo; }
    public void setTipoFluxo(TipoFluxoCME tipoFluxo)           { this.tipoFluxo = tipoFluxo; }
    public ProcessoReprocessamento getProcesso()               { return processo; }
    public void setProcesso(ProcessoReprocessamento processo)  { this.processo = processo; }
    public KitVersion getKitVersao()                           { return kitVersao; }
    public void setKitVersao(KitVersion kitVersao)             { this.kitVersao = kitVersao; }
    public String getDescricaoItem()                           { return descricaoItem; }
    public void setDescricaoItem(String descricaoItem)         { this.descricaoItem = descricaoItem; }
    public CicloEsterilizacao getCicloEsterilizacao()          { return cicloEsterilizacao; }
    public void setCicloEsterilizacao(CicloEsterilizacao c)    { this.cicloEsterilizacao = c; }
    public LocalDateTime getDataHoraInicioMontagem()           { return dataHoraInicioMontagem; }
    public void setDataHoraInicioMontagem(LocalDateTime d)     { this.dataHoraInicioMontagem = d; }
    public LocalDateTime getDataHoraFimMontagem()              { return dataHoraFimMontagem; }
    public void setDataHoraFimMontagem(LocalDateTime d)        { this.dataHoraFimMontagem = d; }
    public LocalDate getDataEmpacotamento()                    { return dataEmpacotamento; }
    public void setDataEmpacotamento(LocalDate d)              { this.dataEmpacotamento = d; }
    public LocalDate getValidade()                             { return validade; }
    public void setValidade(LocalDate validade)                { this.validade = validade; }
    public LoteStatus getStatus()                              { return status; }
    public void setStatus(LoteStatus status)                   { this.status = status; }
    public String getQrCode()                                  { return qrCode; }
    public void setQrCode(String qrCode)                       { this.qrCode = qrCode; }
    public User getMontadoPor()                                { return montadoPor; }
    public void setMontadoPor(User montadoPor)                 { this.montadoPor = montadoPor; }
    public String getObservacoes()                             { return observacoes; }
    public void setObservacoes(String observacoes)             { this.observacoes = observacoes; }
    public LocalDateTime getCriadoEm()                         { return criadoEm; }

    @Override public boolean equals(Object o){ return o instanceof LoteEtiqueta l && Objects.equals(id, l.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
