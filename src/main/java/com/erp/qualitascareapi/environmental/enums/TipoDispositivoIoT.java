package com.erp.qualitascareapi.environmental.enums;

/**
 * Tipo de dispositivo IoT instalado no ambiente hospitalar.
 * Define como a leitura será roteada ao ser recebida via /api/iot/leitura.
 */
public enum TipoDispositivoIoT {
    /** Sensor de temperatura acoplado a uma geladeira de medicamentos/vacinas. */
    TEMPERATURA_GELADEIRA,
    /** Sensor de temperatura/umidade/pressão diferencial em sala ou ambiente controlado. */
    MONITORAMENTO_AMBIENTAL
}
