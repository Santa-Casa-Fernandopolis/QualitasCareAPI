package com.erp.qualitascareapi.iam.api;

import com.erp.qualitascareapi.iam.api.dto.UserDto;
import com.erp.qualitascareapi.iam.api.dto.UserProfileUpdateRequest;
import com.erp.qualitascareapi.iam.application.UserService;
import com.erp.qualitascareapi.security.app.AuthContext;
import com.erp.qualitascareapi.security.app.CurrentUserExtractor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/me")
@PreAuthorize("isAuthenticated()")
public class UserProfileController {

    private final UserService userService;
    private final CurrentUserExtractor currentUserExtractor;

    public UserProfileController(UserService userService,
                                 CurrentUserExtractor currentUserExtractor) {
        this.userService = userService;
        this.currentUserExtractor = currentUserExtractor;
    }

    @GetMapping
    public UserDto getProfile(Authentication authentication) {
        return userService.get(currentUserId(authentication));
    }

    @PutMapping
    public UserDto updateProfile(Authentication authentication,
                                 @Validated @RequestBody UserProfileUpdateRequest request) {
        return userService.updateProfile(currentUserId(authentication), request);
    }

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDto uploadProfilePhoto(Authentication authentication,
                                      @RequestPart("file") MultipartFile file) {
        return userService.uploadPhoto(currentUserId(authentication), file);
    }

    private Long currentUserId(Authentication authentication) {
        AuthContext context = currentUserExtractor.from(authentication);
        if (context.userId() == null) {
            throw new AccessDeniedException("Usuário não autenticado");
        }
        return context.userId();
    }
}
