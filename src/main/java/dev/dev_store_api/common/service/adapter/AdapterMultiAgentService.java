package dev.dev_store_api.common.service.adapter;

import dev.dev_store_api.account.model.Account;
import dev.dev_store_api.auth.model.MultiAgent;
import dev.dev_store_api.auth.repository.MultiAgentRepository;
import dev.dev_store_api.common.exception.AuthException;
import dev.dev_store_api.common.exception.BadRequestException;
import dev.dev_store_api.common.model.type.EMessage;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdapterMultiAgentService {
    private final MultiAgentRepository multiAgentRepository;

    public AdapterMultiAgentService(MultiAgentRepository multiAgentRepository) {
        this.multiAgentRepository = multiAgentRepository;
    }

    public void checkSessionIsActive(MultiAgent session) {
        if (!session.getIsActive()) {
            throw new AuthException(EMessage.DEACTIVATED.getMessage());
        }
    }

    public Optional<MultiAgent> getSessionByAccountAndAgent(Account account, String agent) {
        return multiAgentRepository.findByAccountAndAgent(account, agent);
    }

    public MultiAgent getSessionByRefreshToken(String refreshToken) {
        return multiAgentRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() ->
                        new BadRequestException(EMessage.REFRESH_TOKEN_REQUIRED.getMessage())
                );
    }

    public void createOrUpdateSession(Account account, String agent, String ip, String token, String refreshToken) {
        MultiAgent session = getSessionByAccountAndAgent(account, agent)
                .orElseGet(() -> new MultiAgent(account, agent, ip, token, refreshToken, true));
        checkSessionIsActive(session);
        session.setToken(token);
        session.setRefreshToken(refreshToken);
        multiAgentRepository.save(session);
    }
    public void updateSession(String token, String refreshToken) {
        MultiAgent session = getSessionByRefreshToken(refreshToken);
        checkSessionIsActive(session);
        session.setToken(token);
        session.setRefreshToken(refreshToken);
        multiAgentRepository.save(session);
    }

    public void clearRefreshToken(String refreshToken) {
        MultiAgent session = getSessionByRefreshToken(refreshToken);
        session.setRefreshToken(null);
        multiAgentRepository.save(session);
    }


}
