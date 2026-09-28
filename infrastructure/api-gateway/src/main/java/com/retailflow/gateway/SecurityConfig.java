package com.retailflow.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean
    SecretKey jwtSecretKey(@Value("${security.jwt.secret}") String secret) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("security.jwt.secret must contain at least 32 UTF-8 bytes");
        }
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    ReactiveJwtDecoder jwtDecoder(SecretKey key) {
        return NimbusReactiveJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::authorities);
        return converter;
    }

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                   ReactiveJwtAuthenticationConverter converter) {
        String[] adminOnly = {
                "/api/v1/warehouses/**", "/api/v1/inventory/**", "/api/v1/suppliers/**",
                "/api/v1/purchases/**", "/api/v1/purchase-returns/**", "/api/v1/customers/**",
                "/api/v1/sales/**", "/api/v1/bills/**", "/api/v1/payments/**",
                "/api/v1/reports/**", "/api/v1/notifications/**"
        };
        return http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/products/**", "/api/v1/brands/**").permitAll()
                        .pathMatchers("/actuator/health/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/products/**", "/api/v1/brands/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/products/**", "/api/v1/brands/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/products/**", "/api/v1/brands/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/products/**", "/api/v1/brands/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/v1/payments/razorpay/**").authenticated()
                        .pathMatchers(adminOnly).hasRole("ADMIN")
                        .pathMatchers("/api/v1/users/me", "/api/v1/cart/**", "/api/v1/orders/**").authenticated()
                        .pathMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
                .build();
    }

    private Flux<GrantedAuthority> authorities(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        return roles == null ? Flux.empty() : Flux.fromIterable(roles)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role));
    }
}
