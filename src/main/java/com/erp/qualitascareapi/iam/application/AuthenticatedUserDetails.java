package com.erp.qualitascareapi.iam.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.security.domain.Role;
import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AuthenticatedUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Tenant tenant;
    private final UserStatus status;
    private final IdentityOrigin origin;
    private final String fullName;
    private final String department;
    private final Collection<? extends GrantedAuthority> authorities;
    private final LocalDateTime credentialsExpiry;

    private AuthenticatedUserDetails(Long id,
                                     String username,
                                     String password,
                                     Tenant tenant,
                                     UserStatus status,
                                     IdentityOrigin origin,
                                     String fullName,
                                     String department,
                                     Collection<? extends GrantedAuthority> authorities,
                                     LocalDateTime credentialsExpiry) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.tenant = tenant;
        this.status = status;
        this.origin = origin;
        this.fullName = fullName;
        this.department = department;
        this.authorities = authorities;
        this.credentialsExpiry = credentialsExpiry;
    }

    public static AuthenticatedUserDetails from(User user) {
        Objects.requireNonNull(user, "user must not be null");
        Tenant tenant = Objects.requireNonNull(user.getTenant(), "tenant must not be null");

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .filter(Objects::nonNull)
                .map(Role::getName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .map(String::toUpperCase)
                .map(name -> name.startsWith("ROLE_") ? name : "ROLE_" + name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("TENANT_" + tenant.getId()));

        return new AuthenticatedUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                tenant,
                user.getStatus(),
                user.getOrigin(),
                user.getFullName(),
                user.getDepartment(),
                authorities,
                user.getExpiresAt()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getTenantId() {
        return tenant != null ? tenant.getId() : null;
    }

    public String getTenantCode() {
        return tenant != null && tenant.getCode() != null ? tenant.getCode().toString() : null;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public UserStatus getStatus() {
        return status;
    }

    public IdentityOrigin getOrigin() {
        return origin;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status != UserStatus.EXPIRED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.SUSPENDED && status != UserStatus.DISABLED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        if (credentialsExpiry == null) {
            return true;
        }
        return credentialsExpiry.isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isEnabled() {
        return status != null && status.isActive();
    }
}
