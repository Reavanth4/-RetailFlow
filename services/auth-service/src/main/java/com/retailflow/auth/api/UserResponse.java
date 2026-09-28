package com.retailflow.auth.api;

import com.retailflow.auth.user.UserAccount;

import java.time.LocalDateTime;

public record UserResponse(Long id, String fullName, String email, String role, boolean active,
                           LocalDateTime createdAt) {
    public static UserResponse from(UserAccount user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(),
                user.getRole().name(), user.isActive(), user.getCreatedAt());
    }
}
