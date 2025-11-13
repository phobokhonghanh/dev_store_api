package dev.dev_store_api.auth.service;

import dev.dev_store_api.account.model.Account;
import dev.dev_store_api.auth.dto.MultiAgentResponse;
import dev.dev_store_api.auth.model.MultiAgent;

import java.util.List;
import java.util.Optional;

public interface MultiAgentService {
    boolean isTokenActive(String token);

    void createOrUpdateSession(Account account, String agent, String ip, String token, String refreshToken);
    void updateSession(String token, String refreshToken);
    void deleteSession(String token, Long id);
    void logoutSession(String token, Long id);
    void logoutAllSessions(String token);
    void clearRefreshToken(String refreshToken);

    Optional<MultiAgent> getSessionByAccountAndAgent(Account account, String agent);

    MultiAgent getSessionByRefreshToken(String refreshToken);

    List<MultiAgentResponse> getListSessionsByToken(String token);
}
