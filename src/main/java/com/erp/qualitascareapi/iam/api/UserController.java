package com.erp.qualitascareapi.iam.api;

import com.erp.qualitascareapi.iam.api.dto.UserCreateRequest;
import com.erp.qualitascareapi.iam.api.dto.UserDto;
import com.erp.qualitascareapi.iam.api.dto.UserUpdateRequest;
import com.erp.qualitascareapi.iam.application.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<UserDto> list(Pageable pageable) {
        return userService.list(pageable);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        return userService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Validated @RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @Validated @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
