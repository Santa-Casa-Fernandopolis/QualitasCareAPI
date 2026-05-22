package com.erp.qualitascareapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * Configura o {@link TextEncryptor} AES-256 usado para cifrar valores sensíveis
 * (tipo {@code SECRET}) na tabela {@code sys_configuracoes}.
 *
 * <p>A chave mestra ({@code app.encrypt.key}) e o salt ({@code app.encrypt.salt})
 * NUNCA devem ser versionados. Em produção, injete-os via variáveis de ambiente:</p>
 * <pre>
 *   export ENCRYPT_KEY=&lt;senha-forte-qualquer&gt;
 *   export ENCRYPT_SALT=&lt;16-hex-chars-aleatorios&gt;
 * </pre>
 */
@Configuration
public class EncryptionConfig {

    /**
     * Salt deve ser uma string hexadecimal (mínimo 16 chars = 8 bytes).
     * Geração de exemplo: {@code new SecureRandom().generateSeed(8)} convertido para hex.
     */
    @Bean
    public TextEncryptor textEncryptor(
            @Value("${app.encrypt.key}") String key,
            @Value("${app.encrypt.salt}") String salt) {
        return Encryptors.text(key, salt);
    }
}
