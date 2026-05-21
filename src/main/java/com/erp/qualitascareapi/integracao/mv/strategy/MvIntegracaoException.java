package com.erp.qualitascareapi.integracao.mv.strategy;

/**
 * Exceção lançada quando a integração com o Soul MV falha — seja por
 * indisponibilidade da API REST, erro de autenticação OAuth2 ou falha de
 * conexão JDBC com o banco de dados MV.
 */
public class MvIntegracaoException extends RuntimeException {

    public MvIntegracaoException(String message) {
        super(message);
    }

    public MvIntegracaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
