package com.organizaai.controller;

import com.organizaai.data.dto.request.UpdateUserRequest;
import com.organizaai.data.dto.response.UserResponse;
import com.organizaai.data.entity.User;
import com.organizaai.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal User user) {
        return UserResponse.fromEntity(user);
    }

    @PutMapping("/me")
    public UserResponse updateMe(@AuthenticationPrincipal User user, @Valid @RequestBody UpdateUserRequest request) {
        User updated = userService.updateProfile(user, request);
        return UserResponse.fromEntity(updated);
    }
}
