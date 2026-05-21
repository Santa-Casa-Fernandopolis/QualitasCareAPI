package com.erp.qualitascareapi.security.filter;

import com.erp.qualitascareapi.environmental.domain.DispositivoIoT;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Token de autenticação para dispositivos IoT.
 * Criado pelo {@link IoTDeviceAuthFilter} após validação da chave de API.
 */
public class IoTDeviceAuthentication implements Authentication {

    private final DispositivoIoT device;

    public IoTDeviceAuthentication(DispositivoIoT device) {
        this.device = device;
    }

    public DispositivoIoT getDevice() {
        return device;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_IOT_DEVICE"));
    }

    @Override
    public Object getCredentials() { return null; }

    @Override
    public Object getDetails() { return null; }

    @Override
    public Object getPrincipal() { return device.getDeviceId(); }

    @Override
    public boolean isAuthenticated() { return true; }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {}

    @Override
    public String getName() { return device.getDeviceId(); }
}
