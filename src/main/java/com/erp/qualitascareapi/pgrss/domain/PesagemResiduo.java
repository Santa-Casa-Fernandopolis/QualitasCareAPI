package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.pgrss.enums.StatusPesagem;
import com.erp.qualitascareapi.pgrss.enums.TurnoColeta;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Audited
@Entity
@Table(name = "pgrss_pesagens",
        indexes = {
                @Index(name = "ix_pgrss_pe_tenant_data", columnList = "tenant_id,data_hora_pesagem"),
                @Index(name = "ix_pgrss_pe_setor_data", columnList = "setor_id,data_hora_pesagem")
        })
public class PesagemResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id", nullable = false)
    private SetorGerador setor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoResiduo tipo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    private GrupoResiduo grupo;

    @Column(name = "data_hora_pesagem", nullable = false)
    private LocalDateTime dataHoraPesagem;

    @Column(name = "peso_kg", nullable = false, precision = 10, scale = 3)
    private BigDecimal pesoKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TurnoColeta turno;

    @Column(length = 80)
    private String rota;

    @Column(name = "responsavel_nome", nullable = false, length = 120)
    private String responsavelNome;

    @Column(name = "identificacao_balanca", length = 60)
    private String identificacaoBalanca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPesagem status = StatusPesagem.REGISTRADA;

    @Column(length = 255)
    private String observacoes;

    public PesagemResiduo() {}

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public SetorGerador getSetor() { return setor; }
    public void setSetor(SetorGerador setor) { this.setor = setor; }
    public TipoResiduo getTipo() { return tipo; }
    public void setTipo(TipoResiduo tipo) { this.tipo = tipo; }
    public GrupoResiduo getGrupo() { return grupo; }
    public void setGrupo(GrupoResiduo grupo) { this.grupo = grupo; }
    public LocalDateTime getDataHoraPesagem() { return dataHoraPesagem; }
    public void setDataHoraPesagem(LocalDateTime dataHoraPesagem) { this.dataHoraPesagem = dataHoraPesagem; }
    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }
    public TurnoColeta getTurno() { return turno; }
    public void setTurno(TurnoColeta turno) { this.turno = turno; }
    public String getRota() { return rota; }
    public void setRota(String rota) { this.rota = rota; }
    public String getResponsavelNome() { return responsavelNome; }
    public void setResponsavelNome(String responsavelNome) { this.responsavelNome = responsavelNome; }
    public String getIdentificacaoBalanca() { return identificacaoBalanca; }
    public void setIdentificacaoBalanca(String identificacaoBalanca) { this.identificacaoBalanca = identificacaoBalanca; }
    public StatusPesagem getStatus() { return status; }
    public void setStatus(StatusPesagem status) { this.status = status; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public boolean equals(Object o) { return o instanceof PesagemResiduo p && Objects.equals(id, p.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
