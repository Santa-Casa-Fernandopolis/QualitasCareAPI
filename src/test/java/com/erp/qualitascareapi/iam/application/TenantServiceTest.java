package com.erp.qualitascareapi.iam.application;

import com.erp.qualitascareapi.iam.api.dto.TenantLoginOptionDto;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private UserRepository userRepository;

    private TenantService tenantService;

    @BeforeEach
    void setUp() {
        this.tenantService = new TenantService(tenantRepository, userRepository);
    }

    @Test
    void findAvailableTenantsForUsername_returnsEmptyListWhenUsernameBlank() {
        List<TenantLoginOptionDto> tenants = tenantService.findAvailableTenantsForUsername("   ");

        assertThat(tenants).isEmpty();
        verifyNoInteractions(userRepository);
    }

    @Test
    void findAvailableTenantsForUsername_returnsUniqueActiveTenantsSortedByName() {
        Tenant alpha = new Tenant(1L, 2002L, "Alpha Clinic", "11111111000100", "logo-alpha.png", true);
        Tenant beta = new Tenant(2L, 2001L, "beta hospital", "22222222000100", "logo-beta.png", true);
        Tenant inactive = new Tenant(3L, 2003L, "Gamma Labs", "33333333000100", "logo-gamma.png", false);

        User userAlpha = new User();
        userAlpha.setTenant(alpha);

        User userBeta = new User();
        userBeta.setTenant(beta);

        User userInactive = new User();
        userInactive.setTenant(inactive);

        when(userRepository.findAllByUsernameIgnoreCase("nurse"))
                .thenReturn(List.of(userAlpha, userInactive, userBeta, userAlpha));

        List<TenantLoginOptionDto> tenants = tenantService.findAvailableTenantsForUsername("  nurse  ");

        assertThat(tenants)
                .extracting(TenantLoginOptionDto::name)
                .containsExactly("Alpha Clinic", "beta hospital");
        assertThat(tenants)
                .extracting(TenantLoginOptionDto::code)
                .containsExactly(2002L, 2001L);
    }
}
