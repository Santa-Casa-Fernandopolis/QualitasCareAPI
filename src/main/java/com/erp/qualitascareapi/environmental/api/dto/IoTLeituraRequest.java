package com.erp.qualitascareapi.environmental.api.dto;

import java.time.LocalDateTime;

/**
 * Payload enviado pelo dispositivo IoT (ex.: ESP32) para registrar uma leitura.
 *
 * O roteamento da leitura (geladeira ou monitoramento ambiental) é determinado
 * pelo tipo do dispositivo autenticado via {@code X-Device-Key}.
 *
 * Exemplo mínimo para geladeira:
 * <pre>{"temperaturaC": 4.2}</pre>
 *
 * Exemplo completo para ambiente:
 * <pre>{"temperaturaC": 22.5, "umidade": 55.0, "pressaoPa": -8.0, "dataHora": "2024-01-15T10:30:00"}</pre>
 */
public record IoTLeituraRequest(
        /** Temperatura em graus Celsius. Obrigatório para TEMPERATURA_GELADEIRA. */
        Double temperaturaC,
        /** Umidade relativa em %. Opcional. */
        Double umidade,
        /** Pressão diferencial em Pascal. Opcional, usado em monitoramento ambiental. */
        Double pressaoPa,
        /** Data/hora da leitura. Se null, usa o momento do recebimento. */
        LocalDateTime dataHora
) {}
