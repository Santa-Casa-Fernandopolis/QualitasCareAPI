package com.erp.qualitascareapi.sistema.application;

import com.erp.qualitascareapi.sistema.api.dto.ConfiguracaoDto;
import com.erp.qualitascareapi.sistema.api.dto.ConfiguracaoRequest;
import com.erp.qualitascareapi.sistema.api.dto.ConfiguracaoUpdateRequest;
import com.erp.qualitascareapi.sistema.domain.ConfiguracaoSistema;
import com.erp.qualitascareapi.sistema.enums.ModuloConfiguracao;
import com.erp.qualitascareapi.sistema.enums.TipoValorConfiguracao;
import com.erp.qualitascareapi.sistema.repo.ConfiguracaoSistemaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço de gerenciamento de parâmetros de sistema.
 *
 * <h3>Cache TTL</h3>
 * Leituras são cacheadas em memória por {@value #CACHE_TTL_MS} ms para evitar
 * round-trips ao banco em chamadas frequentes (ex.: resolução de estratégia MV a cada request).
 * O cache é invalidado automaticamente na escrita.
 *
 * <h3>Criptografia</h3>
 * Valores do tipo {@link TipoValorConfiguracao#SECRET} são cifrados com AES-256 antes de
 * persistir e decifrados na leitura. As respostas da API retornam {@code "****"} no lugar
 * do valor real para esse tipo.
 */
@Service
public class ConfiguracaoService {

    static final long CACHE_TTL_MS = 60_000L; // 1 minuto
    private static final String MASCARA_SECRET = "****";

    private final ConfiguracaoSistemaRepository repository;
    private final TextEncryptor encryptor;

    /** Cache em memória: cacheKey → (valorDecifrado, expiresAt). */
    private final ConcurrentHashMap<String, CachedEntry> cache = new ConcurrentHashMap<>();

    private record CachedEntry(String valor, long expiresAt) {
        boolean expirado() { return System.currentTimeMillis() > expiresAt; }
    }

    public ConfiguracaoService(ConfiguracaoSistemaRepository repository,
                               TextEncryptor encryptor) {
        this.repository = repository;
        this.encryptor  = encryptor;
    }

    // ─── Leitura tipada (uso interno pelos outros serviços) ───────────────────

    /**
     * Retorna o valor decifrado de uma configuração global ou {@code null} se não encontrada.
     */
    public String getValor(ModuloConfiguracao modulo, String chave) {
        return getValorInterno(modulo, chave, null);
    }

    /**
     * Valor inteiro de uma configuração global. Empty se ausente.
     */
    public Optional<Integer> getValorInt(ModuloConfiguracao modulo, String chave) {
        String v = getValor(modulo, chave);
        return v == null || v.isBlank() ? Optional.empty() : Optional.of(Integer.parseInt(v.trim()));
    }

    /**
     * Valor booleano de uma configuração global.
     * @param defaultValue valor retornado quando a chave não existe.
     */
    public boolean getValorBoolean(ModuloConfiguracao modulo, String chave, boolean defaultValue) {
        String v = getValor(modulo, chave);
        return v == null || v.isBlank() ? defaultValue : Boolean.parseBoolean(v.trim());
    }

    /**
     * Valor de uma configuração escopada a um tenant específico.
     * Se não encontrada no tenant, faz fallback para a configuração global.
     */
    public String getValorComFallback(ModuloConfiguracao modulo, String chave, Long tenantId) {
        String valorTenant = getValorInterno(modulo, chave, tenantId);
        return valorTenant != null ? valorTenant : getValorInterno(modulo, chave, null);
    }

    // ─── CRUD admin ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ConfiguracaoDto> listarPorModulo(ModuloConfiguracao modulo) {
        return repository.findByModuloAndTenantIdIsNull(modulo)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConfiguracaoDto> listarPorModuloETenant(ModuloConfiguracao modulo, Long tenantId) {
        return repository.findByTenantIdAndModulo(tenantId, modulo)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConfiguracaoDto findById(Long id) {
        return toDto(buscarPorId(id));
    }

    @Transactional
    public ConfiguracaoDto criar(ConfiguracaoRequest request) {
        validarUnicidade(request.tenantId(), request.modulo(), request.chave());
        ConfiguracaoSistema config = new ConfiguracaoSistema();
        config.setTenantId(request.tenantId());
        config.setModulo(request.modulo());
        config.setChave(request.chave());
        config.setTipoValor(request.tipoValor());
        config.setValor(cifrarSeSecret(request.tipoValor(), request.valor()));
        config.setDescricao(request.descricao());
        config.setEditavel(request.editavel());
        return toDto(repository.save(config));
    }

    @Transactional
    public ConfiguracaoDto atualizar(Long id, ConfiguracaoUpdateRequest request) {
        ConfiguracaoSistema config = buscarPorId(id);
        if (!config.isEditavel()) {
            throw new IllegalStateException("Configuração '" + config.getChave() + "' não é editável via API.");
        }
        config.setValor(cifrarSeSecret(config.getTipoValor(), request.valor()));
        invalidarCache(config);
        return toDto(repository.save(config));
    }

    @Transactional
    public void deletar(Long id) {
        ConfiguracaoSistema config = buscarPorId(id);
        if (!config.isEditavel()) {
            throw new IllegalStateException("Configuração '" + config.getChave() + "' não pode ser removida.");
        }
        invalidarCache(config);
        repository.delete(config);
    }

    // ─── Internos ─────────────────────────────────────────────────────────────

    private String getValorInterno(ModuloConfiguracao modulo, String chave, Long tenantId) {
        String key = cacheKey(modulo, chave, tenantId);
        CachedEntry cached = cache.get(key);
        if (cached != null && !cached.expirado()) {
            return cached.valor();
        }

        Optional<ConfiguracaoSistema> opt = tenantId == null
                ? repository.findByModuloAndChaveAndTenantIdIsNull(modulo, chave)
                : repository.findByTenantIdAndModuloAndChave(tenantId, modulo, chave);

        String valor = opt.map(this::decifrarSeSecret).orElse(null);

        if (valor != null) {
            cache.put(key, new CachedEntry(valor, System.currentTimeMillis() + CACHE_TTL_MS));
        }
        return valor;
    }

    private ConfiguracaoSistema buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Configuração não encontrada: id=" + id));
    }

    private void validarUnicidade(Long tenantId, ModuloConfiguracao modulo, String chave) {
        boolean existe = tenantId == null
                ? repository.existsByModuloAndChaveAndTenantIdIsNull(modulo, chave)
                : repository.existsByTenantIdAndModuloAndChave(tenantId, modulo, chave);
        if (existe) {
            throw new IllegalArgumentException(
                    "Já existe configuração para modulo=%s chave=%s tenant=%s"
                            .formatted(modulo, chave, tenantId == null ? "global" : tenantId));
        }
    }

    private String cifrarSeSecret(TipoValorConfiguracao tipo, String valor) {
        if (tipo == TipoValorConfiguracao.SECRET && valor != null && !valor.isBlank()) {
            return encryptor.encrypt(valor);
        }
        return valor;
    }

    private String decifrarSeSecret(ConfiguracaoSistema config) {
        if (config.getTipoValor() == TipoValorConfiguracao.SECRET && config.getValor() != null) {
            try {
                return encryptor.decrypt(config.getValor());
            } catch (Exception e) {
                // Valor ainda não cifrado (migração) — retorna como está
                return config.getValor();
            }
        }
        return config.getValor();
    }

    private void invalidarCache(ConfiguracaoSistema config) {
        cache.remove(cacheKey(config.getModulo(), config.getChave(), config.getTenantId()));
    }

    private String cacheKey(ModuloConfiguracao modulo, String chave, Long tenantId) {
        return (tenantId == null ? "global" : tenantId) + ":" + modulo + ":" + chave;
    }

    private ConfiguracaoDto toDto(ConfiguracaoSistema c) {
        String valorVisivel = c.getTipoValor() == TipoValorConfiguracao.SECRET
                && c.getValor() != null && !c.getValor().isBlank()
                ? MASCARA_SECRET
                : c.getValor();

        return new ConfiguracaoDto(
                c.getId(),
                c.getTenantId(),
                c.getModulo(),
                c.getChave(),
                valorVisivel,
                c.getTipoValor(),
                c.getDescricao(),
                c.isEditavel(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
