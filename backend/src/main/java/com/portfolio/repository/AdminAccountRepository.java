package com.portfolio.repository;

import com.portfolio.model.AdminAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface AdminAccountRepository extends JpaRepository<AdminAccount, Long> {
    Optional<AdminAccount> findByUsernameHashAndActiveTrue(String usernameHash);
    Optional<AdminAccount> findFirstByActiveTrue();
    List<AdminAccount> findAllByActiveTrue();
}
