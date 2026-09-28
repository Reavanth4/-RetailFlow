package com.retailflow.auth.api;

import com.retailflow.auth.user.UserAccountRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserAccountRepository repository;

    public UserController(UserAccountRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return repository.findByEmailIgnoreCase(jwt.getSubject()).map(UserResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user no longer exists"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> allUsers() {
        return repository.findAll().stream().map(UserResponse::from).toList();
    }
}
