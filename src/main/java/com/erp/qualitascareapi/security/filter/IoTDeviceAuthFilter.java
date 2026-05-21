package com.erp.qualitascareapi.security.filter;

import com.erp.qualitascareapi.environmental.domain.DispositivoIoT;
import com.erp.qualitascareapi.environmental.repo.DispositivoIoTRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticação para dispositivos IoT.
 *
 * Valida o header {@code X-Device-Key} na cadeia de segurança dedicada a {@code /api/iot/**}.
 * Se a chave for válida e o dispositivo estiver ativo, define um {@link IoTDeviceAuthentication}
 * no contexto de segurança e passa a requisição adiante.
 *
 * Este filtro NÃO é registrado como @Component para evitar duplo registro no filtro servlet.
 * É instanciado manualmente via bean em {@link com.erp.qualitascareapi.security.config.SecurityConfig}.
 */
public class IoTDeviceAuthFilter extends OncePerRequestFilter {

    public static final String DEVICE_KEY_HEADER = "X-Device-Key";

    private final DispositivoIoTRepository dispositivoRepository;

    public IoTDeviceAuthFilter(DispositivoIoTRepository dispositivoRepository) {
        this.dispositivoRepository = dispositivoRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(DEVICE_KEY_HEADER);

        if (apiKey == null || apiKey.isBlank()) {
            rejectUnauthorized(response, "Header X-Device-Key obrigatório");
            return;
        }

        DispositivoIoT device = dispositivoRepository.findByApiKeyAndAtivoTrue(apiKey).orElse(null);
        if (device == null) {
            rejectUnauthorized(response, "Chave de dispositivo inválida ou dispositivo inativo");
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(new IoTDeviceAuthentication(device));
        filterChain.doFilter(request, response);
    }

    private void rejectUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
