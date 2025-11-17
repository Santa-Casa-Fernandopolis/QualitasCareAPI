package com.erp.qualitascareapi.security.application;

import com.erp.qualitascareapi.security.api.dto.CurrentUserPermissionsResponse;
import com.erp.qualitascareapi.security.app.AuthContext;
import com.erp.qualitascareapi.security.app.CurrentUserExtractor;
import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;
import com.erp.qualitascareapi.security.repo.RolePermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserPermissionsServiceTest {

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Mock
    private CurrentUserExtractor currentUserExtractor;

    private CurrentUserPermissionsService service;

    @BeforeEach
    void setUp() {
        service = new CurrentUserPermissionsService(rolePermissionRepository, currentUserExtractor);
    }

    @Test
    void shouldReturnSortedPermissionCodesForCurrentUser() {
        Authentication authentication = mock(Authentication.class);
        AuthContext context = new AuthContext(
                99L,
                "user.test",
                7L,
                Set.of("ADMIN", "TENANT_7"),
                "Enfermagem",
                null,
                UserStatus.ACTIVE,
                IdentityOrigin.LOCAL,
                Map.of()
        );
        when(currentUserExtractor.from(authentication)).thenReturn(context);
        when(rolePermissionRepository.findPermissionCodesByRolesAndTenant(anySet(), anyLong()))
                .thenReturn(Set.of(" NC_LIST ", "NC_CREATE"));

        CurrentUserPermissionsResponse response = service.getPermissions(authentication);

        assertThat(response.userId()).isEqualTo(99L);
        assertThat(response.username()).isEqualTo("user.test");
        assertThat(response.roles()).containsExactly("ADMIN");
        assertThat(response.permissions()).containsExactly("NC_CREATE", "NC_LIST");

        ArgumentCaptor<Set<String>> rolesCaptor = ArgumentCaptor.forClass(Set.class);
        verify(rolePermissionRepository).findPermissionCodesByRolesAndTenant(rolesCaptor.capture(), anyLong());
        assertThat(rolesCaptor.getValue()).containsExactly("ADMIN");
    }

    @Test
    void shouldFailWhenTenantIsMissing() {
        Authentication authentication = mock(Authentication.class);
        AuthContext context = new AuthContext(
                10L,
                "user.test",
                null,
                Set.of("ADMIN"),
                null,
                null,
                UserStatus.ACTIVE,
                IdentityOrigin.LOCAL,
                Map.of()
        );
        when(currentUserExtractor.from(authentication)).thenReturn(context);

        assertThatThrownBy(() -> service.getPermissions(authentication))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Tenant");
    }
}
