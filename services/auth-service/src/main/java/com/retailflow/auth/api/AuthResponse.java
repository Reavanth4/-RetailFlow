package com.retailflow.auth.api;

public record AuthResponse(String accessToken, String tokenType, long expiresIn, UserResponse user) {
}
