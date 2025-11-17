package com.erp.qualitascareapi.security.api;

import com.erp.qualitascareapi.security.api.dto.CurrentUserPermissionsResponse;
import com.erp.qualitascareapi.security.application.CurrentUserPermissionsService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security/me")
public class CurrentUserController {

    private final CurrentUserPermissionsService currentUserPermissionsService;

    public CurrentUserController(CurrentUserPermissionsService currentUserPermissionsService) {
        this.currentUserPermissionsService = currentUserPermissionsService;
    }

    @GetMapping("/permissions")
    public CurrentUserPermissionsResponse permissions(Authentication authentication) {
        return currentUserPermissionsService.getPermissions(authentication);
    }
}
