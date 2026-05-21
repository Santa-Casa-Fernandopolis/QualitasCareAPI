package com.erp.qualitascareapi.sistema.enums;

/**
 * Tipo semântico do valor de uma configuração de sistema.
 * <ul>
 *   <li>{@link #STRING}  – texto livre</li>
 *   <li>{@link #INTEGER} – número inteiro</li>
 *   <li>{@link #BOOLEAN} – true/false</li>
 *   <li>{@link #SECRET}  – valor sensível — armazenado cifrado com AES; mascarado nas respostas da API</li>
 * </ul>
 */
public enum TipoValorConfiguracao {
    STRING,
    INTEGER,
    BOOLEAN,
    SECRET
}
