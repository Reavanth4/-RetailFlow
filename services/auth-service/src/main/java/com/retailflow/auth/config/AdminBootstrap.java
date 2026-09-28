package com.retailflow.auth.config;

import com.retailflow.auth.user.Role;
import com.retailflow.auth.user.UserAccount;
import com.retailflow.auth.user.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;
    private final String name;

    public AdminBootstrap(UserAccountRepository repository, PasswordEncoder passwordEncoder,
                          @Value("${retailflow.admin.email:}") String email,
                          @Value("${retailflow.admin.password:}") String password,
                          @Value("${retailflow.admin.name:RetailFlow Administrator}") String name) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email.isBlank() || password.isBlank()) {
            return;
        }
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (!repository.existsByEmailIgnoreCase(normalizedEmail)) {
            repository.save(new UserAccount(name.trim(), normalizedEmail,
                    passwordEncoder.encode(password), Role.ADMIN));
        }
    }
}
