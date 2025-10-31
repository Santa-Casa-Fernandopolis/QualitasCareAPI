package com.erp.qualitascareapi.iam.application;

import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LocalUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalUserDetailsService.class);

    private final UserRepository userRepository;

    public LocalUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!StringUtils.hasText(username)) {
            throw new UsernameNotFoundException("Username is required");
        }

        String input = username.trim();
        String tenantCode = null;
        String effectiveUsername = input;

        if (input.contains("@")) {
            int idx = input.indexOf('@');
            effectiveUsername = input.substring(0, idx);
            tenantCode = input.substring(idx + 1);
        } else if (input.contains("|")) {
            int idx = input.indexOf('|');
            tenantCode = input.substring(0, idx);
            effectiveUsername = input.substring(idx + 1);
        }

        if (!StringUtils.hasText(effectiveUsername)) {
            throw new UsernameNotFoundException("Username is required");
        }

        User user = resolveUser(effectiveUsername, tenantCode);
        return AuthenticatedUserDetails.from(user);
    }

    private User resolveUser(String username, String tenantCode) {
        if (StringUtils.hasText(tenantCode)) {
            return userRepository
                    .findByUsernameIgnoreCaseAndTenant_CodeIgnoreCase(username, tenantCode)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found for tenant"));
        }

        var user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("User '{}' resolved without explicit tenant (tenantId={})", username, user.getTenant().getId());
        }

        return user;
    }
}
