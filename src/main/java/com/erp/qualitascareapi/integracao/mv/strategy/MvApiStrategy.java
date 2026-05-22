package com.erp.qualitascareapi.integracao.mv.strategy;

import com.erp.qualitascareapi.integracao.mv.application.MvTokenService;
import com.erp.qualitascareapi.sistema.application.ConfiguracaoService;
import com.erp.qualitascareapi.sistema.enums.ModuloConfiguracao;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Integração com Soul MV via API REST OAuth2.
 *
 * <p>Bean registrado com o qualifier {@code "mv-api"} — selecionado automaticamente
 * pelo {@code MvIntegracaoService} quando {@code MV_INTEGRACAO_TIPO = API}.</p>
 *
 * <h3>Parâmetros usados de sys_configuracoes (módulo MV)</h3>
 * <ul>
 *   <li>{@code MV_API_URL}           — URL base da API (ex.: https://mv.hospital.com.br/api)</li>
 *   <li>{@code MV_API_CLIENT_ID}     — client_id OAuth2</li>
 *   <li>{@code MV_API_CLIENT_SECRET} — client_secret OAuth2 (cifrado)</li>
 *   <li>{@code MV_API_TOKEN_URL}     — endpoint de token OAuth2</li>
 * </ul>
 *
 * <h3>Ajuste para o ambiente</h3>
 * O path {@code /cc/cirurgias} e a estrutura do JSON abaixo são baseados
 * na documentação do Soul MV 4.x. Caso seu hospital use uma versão diferente,
 * ajuste {@link ApiCirurgiaItem} para refletir os campos reais da resposta.
 */
@Component("mv-api")
public class MvApiStrategy implements MvIntegracaoStrategy {

    private static final Logger log = LoggerFactory.getLogger(MvApiStrategy.class);
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final ConfiguracaoService configuracaoService;
    private final MvTokenService tokenService;
    private final RestClient restClient;

    public MvApiStrategy(ConfiguracaoService configuracaoService, MvTokenService tokenService) {
        this.configuracaoService = configuracaoService;
        this.tokenService        = tokenService;
        this.restClient          = RestClient.create();
    }

    // ─── MvIntegracaoStrategy ─────────────────────────────────────────────────

    @Override
    public List<MvCirurgiaRaw> buscarCirurgias(LocalDate dataInicio, LocalDate dataFim) {
        String baseUrl = obterBaseUrl();
        String token   = tokenService.getAccessToken();

        String uri = UriComponentsBuilder.fromUriString(baseUrl + "/cc/cirurgias")
                .queryParam("dataInicio", dataInicio.toString())
                .queryParam("dataFim",    dataFim.toString())
                .queryParam("size",       500)
                .build()
                .toUriString();

        try {
            ApiPageResponse response = restClient.get()
                    .uri(uri)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(ApiPageResponse.class);

            if (response == null || response.content() == null) {
                return List.of();
            }

            log.info("[MV-API] {} cirurgias encontradas entre {} e {}", response.content().size(), dataInicio, dataFim);
            return response.content().stream().map(this::toRaw).toList();

        } catch (Exception e) {
            // Token pode ter expirado — invalida e lança para reprocessamento
            tokenService.invalidarToken();
            throw new MvIntegracaoException("Falha ao buscar cirurgias via API MV: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<MvCirurgiaRaw> buscarPorIdMv(String idMv) {
        String baseUrl = obterBaseUrl();
        String token   = tokenService.getAccessToken();

        try {
            ApiCirurgiaItem item = restClient.get()
                    .uri(baseUrl + "/cc/cirurgias/" + idMv)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(ApiCirurgiaItem.class);

            return Optional.ofNullable(item).map(this::toRaw);

        } catch (Exception e) {
            log.warn("[MV-API] Cirurgia {} não encontrada: {}", idMv, e.getMessage());
            return Optional.empty();
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String obterBaseUrl() {
        String url = configuracaoService.getValor(ModuloConfiguracao.MV, "MV_API_URL");
        if (url == null || url.isBlank()) {
            throw new MvIntegracaoException(
                    "MV_API_URL não configurada — configure em /api/admin/configuracoes (módulo MV)");
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private MvCirurgiaRaw toRaw(ApiCirurgiaItem item) {
        return new MvCirurgiaRaw(
                item.codigoCirurgia(),
                item.codigoPaciente(),
                item.nomePaciente(),
                parseDt(item.dataHoraInicio()),
                parseDt(item.dataHoraFimPrevista()),
                item.descricaoProcedimento(),
                item.salaCirurgica(),
                item.nomeCirurgiao(),
                item.status()
        );
    }

    private LocalDateTime parseDt(String s) {
        return s == null || s.isBlank() ? null : LocalDateTime.parse(s, DT_FMT);
    }

    // ─── DTOs internos do response JSON (ajuste conforme versão do Soul MV) ──

    /**
     * Página paginada retornada pela API MV.
     * Ajuste {@code content} se a API do seu hospital usar outro wrapper (ex.: {@code data}, {@code items}).
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiPageResponse(
            @JsonProperty("content")       List<ApiCirurgiaItem> content,
            @JsonProperty("totalElements") long totalElements
    ) {}

    /**
     * Item de cirurgia conforme Soul MV 4.x.
     * Ajuste os nomes dos campos (@JsonProperty) se sua versão do MV usar nomenclatura diferente.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiCirurgiaItem(
            @JsonProperty("codigoCirurgia")       String codigoCirurgia,
            @JsonProperty("codigoPaciente")        String codigoPaciente,
            @JsonProperty("nomePaciente")          String nomePaciente,
            @JsonProperty("dataHoraInicio")        String dataHoraInicio,
            @JsonProperty("dataHoraFimPrevista")   String dataHoraFimPrevista,
            @JsonProperty("descricaoProcedimento") String descricaoProcedimento,
            @JsonProperty("salaCirurgica")         String salaCirurgica,
            @JsonProperty("nomeCirurgiao")         String nomeCirurgiao,
            @JsonProperty("status")                String status
    ) {}
}
