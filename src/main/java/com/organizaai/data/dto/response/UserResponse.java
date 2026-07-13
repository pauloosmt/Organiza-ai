package com.organizaai.data.dto.response;

import com.organizaai.data.entity.User;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String tema,
        Instant createdAt
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getTema(), user.getCreatedAt());
    }
}
