package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.pgrss.enums.TipoDocumentoResiduo;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.Objects;

@Audited
@Entity
@Table(name = "pgrss_documentos_residuo",
        indexes = {
                @Index(name = "ix_pgrss_dr_coleta_externa", columnList = "coleta_externa_id")
        })
public class DocumentoResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coleta_externa_id")
    private ColetaExterna coletaExterna;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 30)
    private TipoDocumentoResiduo tipoDocumento;

    @Column(name = "nome_arquivo", nullable = false, length = 255)
    private String nomeArquivo;

    @Column(name = "caminho_arquivo", nullable = false, length = 512)
    private String caminhoArquivo;

    @Column(name = "hash_sha256", nullable = false, length = 64)
    private String hashSha256;

    @Column(name = "mime_type", length = 80)
    private String mimeType;

    @Column(name = "tamanho_bytes")
    private Long tamanhoBytes;

    @Column(name = "uploadado_por_nome", length = 120)
    private String uploadadoPorNome;

    @Column(name = "uploadado_em", nullable = false)
    private LocalDateTime uploadadoEm;

    @Column(nullable = false)
    private boolean ativo = true;

    public DocumentoResiduo() {}

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public ColetaExterna getColetaExterna() { return coletaExterna; }
    public void setColetaExterna(ColetaExterna coletaExterna) { this.coletaExterna = coletaExterna; }
    public TipoDocumentoResiduo getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumentoResiduo tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }
    public String getCaminhoArquivo() { return caminhoArquivo; }
    public void setCaminhoArquivo(String caminhoArquivo) { this.caminhoArquivo = caminhoArquivo; }
    public String getHashSha256() { return hashSha256; }
    public void setHashSha256(String hashSha256) { this.hashSha256 = hashSha256; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Long getTamanhoBytes() { return tamanhoBytes; }
    public void setTamanhoBytes(Long tamanhoBytes) { this.tamanhoBytes = tamanhoBytes; }
    public String getUploadadoPorNome() { return uploadadoPorNome; }
    public void setUploadadoPorNome(String uploadadoPorNome) { this.uploadadoPorNome = uploadadoPorNome; }
    public LocalDateTime getUploadadoEm() { return uploadadoEm; }
    public void setUploadadoEm(LocalDateTime uploadadoEm) { this.uploadadoEm = uploadadoEm; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public boolean equals(Object o) { return o instanceof DocumentoResiduo d && Objects.equals(id, d.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
