package dev.dev_store_api.repository;

import dev.dev_store_api.model.Account;
import dev.dev_store_api.model.MultiAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MultiAgentRepository extends JpaRepository<MultiAgent, Long> {
    Optional<MultiAgent> findByRefreshToken(String refreshToken);
    Optional<MultiAgent> findByAccountAndAgent(Account account, String agent);
    boolean existsByTokenAndIsActiveTrue(String token);
}