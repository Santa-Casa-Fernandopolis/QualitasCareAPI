package com.erp.qualitascareapi.integracao.mv.strategy;

import com.erp.qualitascareapi.sistema.application.ConfiguracaoService;
import com.erp.qualitascareapi.sistema.enums.ModuloConfiguracao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Integração com Soul MV via leitura direta no banco de dados (JDBC).
 *
 * <p>Bean registrado com o qualifier {@code "mv-banco"} — selecionado automaticamente
 * pelo {@code MvIntegracaoService} quando {@code MV_INTEGRACAO_TIPO = BANCO_DADOS}.</p>
 *
 * <h3>Parâmetros usados de sys_configuracoes (módulo MV)</h3>
 * <ul>
 *   <li>{@code MV_DB_URL}              — JDBC URL (ex.: {@code jdbc:sqlserver://srv:1433;databaseName=MVSOUL})</li>
 *   <li>{@code MV_DB_USERNAME}         — usuário de leitura</li>
 *   <li>{@code MV_DB_PASSWORD}         — senha (cifrada no banco)</li>
 *   <li>{@code MV_DB_QUERY_CIRURGIAS}  — SQL customizado (opcional; usa query padrão se ausente)</li>
 * </ul>
 *
 * <h3>Query padrão</h3>
 * A query embutida abaixo é baseada no schema comum do Soul MV em SQL Server.
 * Ajuste a constante {@link #QUERY_PADRAO} ou configure {@code MV_DB_QUERY_CIRURGIAS}
 * em {@code sys_configuracoes} para sobrescrever sem alterar o código.
 *
 * <p>A query deve retornar as colunas: {@code id_mv, codigo_paciente, nome_paciente,
 * data_hora_inicio, data_hora_fim_prevista, tipo_cirurgia, sala_cirurgica, nome_cirurgiao, status_mv}.</p>
 *
 * <h3>Driver JDBC</h3>
 * Para SQL Server, adicione ao pom.xml:
 * <pre>{@code
 * <dependency>
 *     <groupId>com.microsoft.sqlserver</groupId>
 *     <artifactId>mssql-jdbc</artifactId>
 *     <version>12.8.1.jre11</version>
 * </dependency>
 * }</pre>
 */
@Component("mv-banco")
public class MvBancoDadosStrategy implements MvIntegracaoStrategy {

    private static final Logger log = LoggerFactory.getLogger(MvBancoDadosStrategy.class);

    /**
     * Query padrão baseada no schema Soul MV SQL Server.
     * Parâmetros nomeados: {@code :dataInicio} e {@code :dataFim} (LocalDate).
     *
     * <p><b>Ajuste conforme o schema do seu hospital</b> — aliases das colunas
     * devem corresponder exatamente aos nomes listados no Javadoc da classe.</p>
     */
    static final String QUERY_PADRAO = """
            SELECT
                CAST(c.CD_CIRURGIA   AS VARCHAR(40)) AS id_mv,
                CAST(p.CD_PACIENTE   AS VARCHAR(40)) AS codigo_paciente,
                p.NM_PACIENTE                        AS nome_paciente,
                c.DT_INICIO                          AS data_hora_inicio,
                c.DT_FIM_PREV                        AS data_hora_fim_prevista,
                tc.DS_TIPO_CIR                       AS tipo_cirurgia,
                sc.DS_SALA                           AS sala_cirurgica,
                prof.NM_PROFISSIONAL                 AS nome_cirurgiao,
                c.DS_STATUS                          AS status_mv
            FROM       CC_CIRURGIA        c
            JOIN       PEP_PACIENTE       p    ON p.CD_PACIENTE    = c.CD_PACIENTE
            JOIN       CC_TIPO_CIRURGIA   tc   ON tc.CD_TIPO_CIR   = c.CD_TIPO_CIR
            JOIN       CC_SALA_CIRURGICA  sc   ON sc.CD_SALA       = c.CD_SALA
            LEFT JOIN  PROFISSIONAL       prof ON prof.CD_PROFISSIONAL = c.CD_CIRURGIAO
            WHERE c.DT_INICIO >= :dataInicio
              AND c.DT_INICIO <= :dataFim
              AND c.DS_STATUS NOT IN ('CANCELADA', 'SUSPENSA')
            ORDER BY c.DT_INICIO
            """;

    private final ConfiguracaoService configuracaoService;

    public MvBancoDadosStrategy(ConfiguracaoService configuracaoService) {
        this.configuracaoService = configuracaoService;
    }

    // ─── MvIntegracaoStrategy ─────────────────────────────────────────────────

    @Override
    public List<MvCirurgiaRaw> buscarCirurgias(LocalDate dataInicio, LocalDate dataFim) {
        NamedParameterJdbcTemplate jdbc = criarJdbcTemplate();
        String sql = resolverQuery();

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("dataInicio", dataInicio.atStartOfDay())
                .addValue("dataFim",    dataFim.atTime(23, 59, 59));

        try {
            List<MvCirurgiaRaw> resultado = jdbc.query(sql, params, (rs, rowNum) -> mapRow(rs));
            log.info("[MV-BANCO] {} cirurgias encontradas entre {} e {}", resultado.size(), dataInicio, dataFim);
            return resultado;
        } catch (Exception e) {
            throw new MvIntegracaoException(
                    "Falha ao consultar cirurgias no banco MV: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<MvCirurgiaRaw> buscarPorIdMv(String idMv) {
        NamedParameterJdbcTemplate jdbc = criarJdbcTemplate();

        String sql = """
                SELECT
                    CAST(c.CD_CIRURGIA   AS VARCHAR(40)) AS id_mv,
                    CAST(p.CD_PACIENTE   AS VARCHAR(40)) AS codigo_paciente,
                    p.NM_PACIENTE                        AS nome_paciente,
                    c.DT_INICIO                          AS data_hora_inicio,
                    c.DT_FIM_PREV                        AS data_hora_fim_prevista,
                    tc.DS_TIPO_CIR                       AS tipo_cirurgia,
                    sc.DS_SALA                           AS sala_cirurgica,
                    prof.NM_PROFISSIONAL                 AS nome_cirurgiao,
                    c.DS_STATUS                          AS status_mv
                FROM       CC_CIRURGIA        c
                JOIN       PEP_PACIENTE       p    ON p.CD_PACIENTE      = c.CD_PACIENTE
                JOIN       CC_TIPO_CIRURGIA   tc   ON tc.CD_TIPO_CIR     = c.CD_TIPO_CIR
                JOIN       CC_SALA_CIRURGICA  sc   ON sc.CD_SALA         = c.CD_SALA
                LEFT JOIN  PROFISSIONAL       prof ON prof.CD_PROFISSIONAL = c.CD_CIRURGIAO
                WHERE CAST(c.CD_CIRURGIA AS VARCHAR(40)) = :idMv
                """;

        try {
            List<MvCirurgiaRaw> lista = jdbc.query(sql,
                    new MapSqlParameterSource("idMv", idMv),
                    (rs, rowNum) -> mapRow(rs));
            return lista.stream().findFirst();
        } catch (Exception e) {
            log.warn("[MV-BANCO] Cirurgia {} não encontrada: {}", idMv, e.getMessage());
            return Optional.empty();
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Cria um {@link NamedParameterJdbcTemplate} com parâmetros lidos em runtime
     * de {@code sys_configuracoes}. Um novo DataSource é criado a cada sync
     * (conexão MV é eventual, não precisa de pool).
     */
    private NamedParameterJdbcTemplate criarJdbcTemplate() {
        String url      = obterConfig("MV_DB_URL");
        String username = obterConfig("MV_DB_USERNAME");
        String password = obterConfig("MV_DB_PASSWORD");

        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);

        return new NamedParameterJdbcTemplate(ds);
    }

    private String resolverQuery() {
        String customQuery = configuracaoService.getValor(ModuloConfiguracao.MV, "MV_DB_QUERY_CIRURGIAS");
        return (customQuery != null && !customQuery.isBlank()) ? customQuery : QUERY_PADRAO;
    }

    private String obterConfig(String chave) {
        String valor = configuracaoService.getValor(ModuloConfiguracao.MV, chave);
        if (valor == null || valor.isBlank()) {
            throw new MvIntegracaoException(
                    "Parâmetro obrigatório não configurado: " + chave +
                    " — configure em /api/admin/configuracoes (módulo MV)");
        }
        return valor;
    }

    private MvCirurgiaRaw mapRow(ResultSet rs) throws SQLException {
        return new MvCirurgiaRaw(
                rs.getString("id_mv"),
                rs.getString("codigo_paciente"),
                rs.getString("nome_paciente"),
                toLocalDateTime(rs.getTimestamp("data_hora_inicio")),
                toLocalDateTime(rs.getTimestamp("data_hora_fim_prevista")),
                rs.getString("tipo_cirurgia"),
                rs.getString("sala_cirurgica"),
                rs.getString("nome_cirurgiao"),
                rs.getString("status_mv")
        );
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }
}
