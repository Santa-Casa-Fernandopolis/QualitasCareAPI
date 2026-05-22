package com.erp.qualitascareapi.environmental.api;

import com.erp.qualitascareapi.environmental.api.dto.IoTLeituraRequest;
import com.erp.qualitascareapi.environmental.api.dto.IoTLeituraResponse;
import com.erp.qualitascareapi.environmental.application.EnvironmentalService;
import com.erp.qualitascareapi.security.filter.IoTDeviceAuthentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints de ingestão de dados IoT.
 *
 * Autenticação: header {@code X-Device-Key} com a chave gerada no cadastro do dispositivo.
 * Não requer JWT — a cadeia de segurança IoT (Order 1) trata a autenticação antes.
 *
 * <pre>
 * curl -X POST https://api.qualitascare.com/api/iot/leitura \
 *      -H "X-Device-Key: abc123..." \
 *      -H "Content-Type: application/json" \
 *      -d '{"temperaturaC": 4.2, "umidade": 65.0}'
 * </pre>
 */
@RestController
@RequestMapping("/api/iot")
public class IoTController {

    private final EnvironmentalService environmentalService;

    public IoTController(EnvironmentalService environmentalService) {
        this.environmentalService = environmentalService;
    }

    /**
     * Recebe uma leitura de sensor IoT e persiste o registro correspondente.
     * O roteamento (geladeira ou monitoramento ambiental) é determinado pelo tipo
     * do dispositivo autenticado.
     */
    @PostMapping("/leitura")
    @ResponseStatus(HttpStatus.CREATED)
    public IoTLeituraResponse processarLeitura(@RequestBody IoTLeituraRequest request) {
        IoTDeviceAuthentication auth =
                (IoTDeviceAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return environmentalService.processarLeituraIoT(auth.getDevice(), request);
    }
}
