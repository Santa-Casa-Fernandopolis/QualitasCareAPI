package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.app.target.TargetNotFoundException;
import com.erp.qualitascareapi.security.app.target.UserTarget;
import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class TargetLoaderTest {

    @Autowired
    private TargetLoader targetLoader;

    @SpyBean
    private UserRepository userRepository;

    private User adminScf;
    private AuthContext adminContext;

    @BeforeEach
    void setUpContext() {
        adminScf = userRepository.findByUsernameIgnoreCase("admin.scf")
                .orElseThrow(() -> new IllegalStateException("Seed user admin.scf not found"));
        adminContext = new AuthContext(
                adminScf.getId(),
                adminScf.getUsername(),
                adminScf.getTenant().getId(),
                Set.of("ADMIN_QUALIDADE"),
                adminScf.getDepartment(),
                null,
                adminScf.getStatus(),
                adminScf.getOrigin(),
                Map.of()
        );
    }

    @AfterEach
    void clearRequestAttributes() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldResolveUserTargetUsingTenantScope() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Object resolved = targetLoader.load("user", adminScf.getId(), adminContext);

        assertThat(resolved).isInstanceOf(UserTarget.class);
        UserTarget userTarget = (UserTarget) resolved;
        assertThat(userTarget.getTenantId()).isEqualTo(adminScf.getTenant().getId());
        assertThat(userTarget.getDepartment()).isEqualTo(adminScf.getDepartment());
        assertThat(userTarget.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(userTarget.getOrigin()).isEqualTo(IdentityOrigin.LOCAL);
        assertThat(userTarget.getOwnerId()).isEqualTo(adminScf.getUsername());
    }

    @Test
    void shouldCacheResolvedTargetWithinSameRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Mockito.clearInvocations(userRepository);

        Object first = targetLoader.load("USER", adminScf.getId(), adminContext);
        Object second = targetLoader.load("USER", adminScf.getId(), adminContext);

        assertThat(second).isSameAs(first);
        Mockito.verify(userRepository, Mockito.times(1))
                .findAuthorizationProjectionByIdAndTenantId(adminScf.getId(), adminScf.getTenant().getId());
    }

    @Test
    void shouldThrowWhenTargetNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertThatThrownBy(() -> targetLoader.load("USER", Long.MAX_VALUE, adminContext))
                .isInstanceOf(TargetNotFoundException.class);
    }
}
