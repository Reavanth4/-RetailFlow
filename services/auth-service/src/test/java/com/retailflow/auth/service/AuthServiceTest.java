package com.retailflow.auth.service;

import com.retailflow.auth.api.AuthResponse;
import com.retailflow.auth.api.LoginRequest;
import com.retailflow.auth.api.RegisterRequest;
import com.retailflow.auth.user.Role;
import com.retailflow.auth.user.UserAccount;
import com.retailflow.auth.user.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock private UserAccountRepository repository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtEncoder jwtEncoder;

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(repository, passwordEncoder, jwtEncoder, Duration.ofHours(2), "retailflow-test");
        Jwt jwt = new Jwt("signed-token", Instant.now(), Instant.now().plusSeconds(7200),
                java.util.Map.of("alg", "HS256"), java.util.Map.of("sub", "customer@example.com"));
        lenient().when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
    }

    @Test
    void registrationNormalizesEmailAndCanOnlyCreateUserRole() {
        when(repository.existsByEmailIgnoreCase("customer@example.com")).thenReturn(false);
        when(passwordEncoder.encode("strong-pass")).thenReturn("encoded");
        when(repository.save(any(UserAccount.class))).thenAnswer(invocation -> {
            UserAccount user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "id", 42L);
            return user;
        });

        AuthResponse response = service.register(new RegisterRequest(
                "  Retail Customer  ", " Customer@Example.COM ", "strong-pass"));

        ArgumentCaptor<UserAccount> saved = ArgumentCaptor.forClass(UserAccount.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().getEmail()).isEqualTo("customer@example.com");
        assertThat(saved.getValue().getFullName()).isEqualTo("Retail Customer");
        assertThat(saved.getValue().getRole()).isEqualTo(Role.USER);
        assertThat(response.accessToken()).isEqualTo("signed-token");
        assertThat(response.user().id()).isEqualTo(42L);
    }

    @Test
    void duplicateRegistrationIsRejected() {
        when(repository.existsByEmailIgnoreCase("customer@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.register(new RegisterRequest(
                "Customer", "customer@example.com", "strong-pass")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
        verify(repository, never()).save(any());
    }

    @Test
    void loginRejectsWrongPasswordWithoutRevealingWhichCredentialFailed() {
        UserAccount user = new UserAccount("Customer", "customer@example.com", "encoded", Role.USER);
        when(repository.findByEmailIgnoreCase("customer@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-pass", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> service.login(new LoginRequest("customer@example.com", "wrong-pass")))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password");
    }
}
