package com.erp.qualitascareapi.iam.api;

import com.erp.qualitascareapi.iam.api.dto.UserDto;
import com.erp.qualitascareapi.iam.api.dto.UserProfileUpdateRequest;
import com.erp.qualitascareapi.iam.application.AuthenticatedUserDetails;
import com.erp.qualitascareapi.iam.application.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserDto getProfile(@AuthenticationPrincipal AuthenticatedUserDetails currentUser) {
        return userService.get(currentUser.getId());
    }

    @PutMapping
    public UserDto updateProfile(@AuthenticationPrincipal AuthenticatedUserDetails currentUser,
                                 @Validated @RequestBody UserProfileUpdateRequest request) {
        return userService.updateProfile(currentUser.getId(), request);
    }
}
