package com.portfolio.config;

import com.portfolio.model.AdminAccount;
import com.portfolio.repository.AdminAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {
    private final AdminAccountRepository accounts;

    @Value("${app.admin.username:}")
    private String adminUsername;

    @Override
    public void run(String... args) throws Exception {
        if (adminUsername == null || adminUsername.isBlank()) {
            log.warn("Admin login is disabled because ADMIN_USERNAME is not configured");
            return;
        }

        String usernameHash = HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(adminUsername.getBytes(StandardCharsets.UTF_8)));
        var activeAccounts = accounts.findAllByActiveTrue();
        AdminAccount account = activeAccounts.isEmpty() ? new AdminAccount() : activeAccounts.get(0);
        activeAccounts.stream().skip(1).forEach(existing -> existing.setActive(false));
        accounts.saveAll(activeAccounts.stream().skip(1).toList());
        account.setUsernameHash(usernameHash);
        account.setActive(true);
        accounts.save(account);
        log.info("Admin account is ready; only its SHA-256 identifier is stored in the database");
    }
}
