package com.erp.qualitascareapi.security.app;

import java.util.Set;

public record AuthContext(Long userId, Long tenantId, Set<String> roles, String department) {}

