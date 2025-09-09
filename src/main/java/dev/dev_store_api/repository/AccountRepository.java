package dev.dev_store_api.repository;

import dev.dev_store_api.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Repository cho Account
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
