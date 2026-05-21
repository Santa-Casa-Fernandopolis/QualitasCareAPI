package com.erp.qualitascareapi.integracao.mv.strategy;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Contrato para integração com o Soul MV — independente da fonte de dados.
 *
 * <h3>Implementações disponíveis</h3>
 * <ul>
 *   <li>{@code mv-api}    — {@link MvApiStrategy}: consome a API REST OAuth2 do Soul MV</li>
 *   <li>{@code mv-banco}  — {@link MvBancoDadosStrategy}: leitura direta via JDBC no banco MV</li>
 * </ul>
 *
 * A escolha é feita em runtime pelo {@code MvIntegracaoService} com base no parâmetro
 * {@code MV_INTEGRACAO_TIPO} em {@code sys_configuracoes}.
 */
public interface MvIntegracaoStrategy {

    /**
     * Retorna cirurgias agendadas no intervalo de datas informado.
     *
     * @param dataInicio início do período (inclusive)
     * @param dataFim    fim do período (inclusive)
     * @return lista de cirurgias — nunca null, pode ser vazia
     * @throws MvIntegracaoException se a fonte MV não estiver acessível
     */
    List<MvCirurgiaRaw> buscarCirurgias(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca uma cirurgia específica pelo identificador MV.
     *
     * @param idMv identificador da cirurgia no sistema MV
     * @return cirurgia encontrada ou {@link Optional#empty()} se não existir
     */
    Optional<MvCirurgiaRaw> buscarPorIdMv(String idMv);
}
