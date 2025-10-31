package com.erp.qualitascareapi.iam.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.domains.Role;
import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private LocalUserDetailsService service;

    private Tenant tenant;

    @BeforeEach
    void setUp() {
        this.service = new LocalUserDetailsService(userRepository);
        this.tenant = new Tenant(1L, "SCF", "Santa Casa Felicidade", true);
    }

    @Test
    void loadUserByUsername_resolvesTenantSuffixWithAtSymbol() {
        User user = buildUser("enf.scf");
        when(userRepository.findByUsernameIgnoreCaseAndTenant_CodeIgnoreCase("enf.scf", "scf"))
                .thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("  enf.scf@scf  ");

        assertThat(details).isInstanceOf(AuthenticatedUserDetails.class);
        assertThat(details.getUsername()).isEqualTo("enf.scf");

        verify(userRepository).findByUsernameIgnoreCaseAndTenant_CodeIgnoreCase("enf.scf", "scf");
        verify(userRepository, never()).findByUsernameIgnoreCase("enf.scf");
    }

    @Test
    void loadUserByUsername_resolvesTenantPrefixSeparatedByPipe() {
        User user = buildUser("admin.scf");
        when(userRepository.findByUsernameIgnoreCaseAndTenant_CodeIgnoreCase("admin.scf", "SCF"))
                .thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("SCF|admin.scf");

        assertThat(details.getUsername()).isEqualTo("admin.scf");

        ArgumentCaptor<String> tenantCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).findByUsernameIgnoreCaseAndTenant_CodeIgnoreCase("admin.scf", tenantCaptor.capture());
        assertThat(tenantCaptor.getValue()).isEqualTo("SCF");
        verify(userRepository, never()).findByUsernameIgnoreCase("admin.scf");
    }

    @Test
    void loadUserByUsername_withoutTenantFallsBackToGenericLookup() {
        User user = buildUser("enf.scf");
        when(userRepository.findByUsernameIgnoreCase("enf.scf"))
                .thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("enf.scf");

        assertThat(details.getUsername()).isEqualTo("enf.scf");
        verify(userRepository).findByUsernameIgnoreCase("enf.scf");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void loadUserByUsername_rejectsBlankInput() {
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("   "));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void loadUserByUsername_throwsWhenRepositoryReturnsEmpty() {
        when(userRepository.findByUsernameIgnoreCase("ghost"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("ghost"));
    }

    private User buildUser(String username) {
        User user = new User();
        user.setId(99L);
        user.setUsername(username);
        user.setPasswordHash("{noop}secret");
        user.setTenant(tenant);
        user.setStatus(UserStatus.ACTIVE);
        user.setOrigin(IdentityOrigin.LOCAL);
        user.setDepartment("UTI");
        user.setActivatedAt(LocalDateTime.now());
        user.getRoles().add(new Role(10L, "ENFERMEIRO", tenant, "Enfermagem"));
    @InjectMocks
    private LocalUserDetailsService service;

    @Test
    void shouldResolveUserWithExplicitTenantSuffix() {
        Tenant tenant = new Tenant(1L, "SCF", "Santa Casa Felicidade", true);
        Role role = new Role(2L, "ADMIN_QUALIDADE", tenant, "Administrador");
        User user = buildUser(tenant, role);

        when(userRepository.findByUsernameIgnoreCaseAndTenant_CodeIgnoreCase("admin", "SCF"))
                .thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("admin@SCF");

        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_ADMIN_QUALIDADE", "TENANT_1");

        verify(userRepository, never()).findByUsernameIgnoreCase(anyString());
    }

    @Test
    void shouldResolveUserWhenTenantNotProvided() {
        Tenant tenant = new Tenant(5L, "SCJ", "Santa Casa Jacarandá", true);
        Role role = new Role(3L, "ENFERMEIRO", tenant, "Enfermeiro");
        User user = buildUser(tenant, role);

        when(userRepository.findByUsernameIgnoreCase("enf.scj"))
                .thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("enf.scj");

        assertThat(result.getUsername()).isEqualTo("enf.scj");
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_ENFERMEIRO", "TENANT_5");

        verify(userRepository).findByUsernameIgnoreCase("enf.scj");
    }

    @Test
    void shouldRejectBlankUsernames() {
        assertThatThrownBy(() -> service.loadUserByUsername(" "))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Username is required");
    }

    private User buildUser(Tenant tenant, Role role) {
        User user = new User();
        user.setId(10L);
        user.setUsername("admin");
        user.setPasswordHash("secret");
        user.setTenant(tenant);
        user.setDepartment("Qualidade");
        user.setStatus(UserStatus.ACTIVE);
        user.setOrigin(IdentityOrigin.LOCAL);
        user.getRoles().add(role);
        return user;
    }
}
