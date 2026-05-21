package com.erp.qualitascareapi.integracao.mv.application;

import com.erp.qualitascareapi.integracao.mv.strategy.MvIntegracaoException;
import com.erp.qualitascareapi.sistema.application.ConfiguracaoService;
import com.erp.qualitascareapi.sistema.enums.ModuloConfiguracao;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;

/**
 * Gerencia o token OAuth2 (client_credentials) para autenticação na API REST do Soul MV.
 *
 * <p>O token é obtido na primeira chamada e cacheado até 30 segundos antes do seu vencimento,
 * evitando round-trips desnecessários ao servidor de autorização MV.</p>
 *
 * <p>Credenciais lidas em runtime de {@code sys_configuracoes}:
 * {@code MV_API_TOKEN_URL}, {@code MV_API_CLIENT_ID}, {@code MV_API_CLIENT_SECRET}.</p>
 */
@Service
public class MvTokenService {

    private static final Logger log = LoggerFactory.getLogger(MvTokenService.class);
    private static final int MARGEM_EXPIRACAO_SEG = 30;

    private final ConfiguracaoService configuracaoService;
    private final RestClient restClient;

    private volatile TokenCache tokenCache;

    private record TokenCache(String accessToken, Instant expiresAt) {
        boolean valido() {
            return Instant.now().isBefore(expiresAt.minusSeconds(MARGEM_EXPIRACAO_SEG));
        }
    }

    /** Estrutura do response JSON do endpoint de token OAuth2. */
    private record TokenResponse(
            @JsonProperty("access_token")  String accessToken,
            @JsonProperty("expires_in")    long expiresIn,
            @JsonProperty("token_type")    String tokenType
    ) {}

    public MvTokenService(ConfiguracaoService configuracaoService) {
        this.configuracaoService = configuracaoService;
        this.restClient = RestClient.create();
    }

    /**
     * Retorna um access token válido, obtendo um novo se necessário.
     *
     * @throws MvIntegracaoException se as credenciais não estiverem configuradas ou o
     *                               servidor de autorização MV retornar erro
     */
    public String getAccessToken() {
        if (tokenCache != null && tokenCache.valido()) {
            return tokenCache.accessToken();
        }
        return fetchNovoToken();
    }

    /** Invalida o token em cache, forçando renovação na próxima chamada. */
    public synchronized void invalidarToken() {
        tokenCache = null;
    }

    private synchronized String fetchNovoToken() {
        // Double-check após aquisição do lock
        if (tokenCache != null && tokenCache.valido()) {
            return tokenCache.accessToken();
        }

        String tokenUrl  = configuracaoService.getValor(ModuloConfiguracao.MV, "MV_API_TOKEN_URL");
        String clientId  = configuracaoService.getValor(ModuloConfiguracao.MV, "MV_API_CLIENT_ID");
        String clientSecret = configuracaoService.getValor(ModuloConfiguracao.MV, "MV_API_CLIENT_SECRET");

        validarConfiguracao(tokenUrl, "MV_API_TOKEN_URL");
        validarConfiguracao(clientId, "MV_API_CLIENT_ID");
        validarConfiguracao(clientSecret, "MV_API_CLIENT_SECRET");

        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type",    "client_credentials");
            formData.add("client_id",     clientId);
            formData.add("client_secret", clientSecret);

            TokenResponse response = restClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(TokenResponse.class);

            if (response == null || response.accessToken() == null) {
                throw new MvIntegracaoException("Resposta inválida do servidor OAuth2 MV");
            }

            tokenCache = new TokenCache(
                    response.accessToken(),
                    Instant.now().plusSeconds(response.expiresIn())
            );

            log.info("Token OAuth2 MV renovado — expira em {}s", response.expiresIn());
            return tokenCache.accessToken();

        } catch (MvIntegracaoException e) {
            throw e;
        } catch (Exception e) {
            throw new MvIntegracaoException("Falha ao obter token OAuth2 do Soul MV: " + e.getMessage(), e);
        }
    }

    private void validarConfiguracao(String valor, String chave) {
        if (valor == null || valor.isBlank()) {
            throw new MvIntegracaoException(
                    "Parâmetro obrigatório não configurado: " + chave +
                    " — configure em /api/admin/configuracoes (módulo MV)");
        }
    }
}
