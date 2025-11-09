package dev.dev_store_api.auth.service;

import dev.dev_store_api.account.model.Account;
import dev.dev_store_api.account.repository.AccountRepository;
import dev.dev_store_api.account.service.AccountService;
import dev.dev_store_api.auth.dto.MultiAgentResponse;
import dev.dev_store_api.auth.model.MultiAgent;
import dev.dev_store_api.auth.repository.MultiAgentRepository;
import dev.dev_store_api.common.exception.AuthException;
import dev.dev_store_api.common.exception.BadRequestException;
import dev.dev_store_api.common.exception.NotFoundException;
import dev.dev_store_api.common.model.type.EMessage;
import dev.dev_store_api.common.util.GenericMapper;
import jakarta.validation.constraints.NotNull;
import org.aspectj.weaver.loadtime.Agent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MultiAgentService {
    private final MultiAgentRepository multiAgentRepository;
    private final AccountService  accountService;
    private final GenericMapper genericMapper;

    public MultiAgentService(MultiAgentRepository multiAgentRepository, AccountService accountService, GenericMapper genericMapper) {
        this.multiAgentRepository = multiAgentRepository;
        this.accountService = accountService;
        this.genericMapper = genericMapper;
    }

    public boolean checkToken(String token) {
        return multiAgentRepository.existsByTokenAndIsActiveTrue(token);
    }

    public MultiAgent getSessionByRefresh(String refreshToken) {
        return multiAgentRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() ->
                        new BadRequestException(EMessage.REFRESH_TOKEN_REQUIRED.getMessage())
                );
    }

    public Optional<MultiAgent> getSessionByAccountAndAgent(Account account, String agent) {
        return multiAgentRepository.findByAccountAndAgent(account, agent);
    }

    public void clearRefreshToken(String refreshToken) {
        MultiAgent session = getSessionByRefresh(refreshToken);
        session.setRefreshToken(null);
        multiAgentRepository.save(session);
    }

    public void checkSessionIsActive(MultiAgent session) {
        if (!session.getIsActive()) {
            throw new AuthException(EMessage.DEACTIVATED.getMessage());
        }
    }

    public void updateSession(String token, String refreshToken) {
        MultiAgent session = getSessionByRefresh(refreshToken);
        checkSessionIsActive(session);
        session.setToken(token);
        session.setRefreshToken(refreshToken);
        multiAgentRepository.save(session);
    }

    public void createOrUpdateSession(Account account, String agent, String ip, String token, String refreshToken) {
        MultiAgent session = getSessionByAccountAndAgent(account, agent)
                .orElseGet(() -> new MultiAgent(account, agent, ip, token, refreshToken, true));
        checkSessionIsActive(session);
        session.setToken(token);
        session.setRefreshToken(refreshToken);
        multiAgentRepository.save(session);
    }

    public List<MultiAgentResponse> getSessionsByToken(String token) {
        Account account = accountService.getAccountByToken(token);
        List<MultiAgent> listMultiAgent = multiAgentRepository.findByAccount(account);
        return genericMapper.toDTOList(listMultiAgent,MultiAgentResponse.class);
    }

    public MultiAgent getSessionById(Long id) {
        return multiAgentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(EMessage.NOT_FOUND.format("session")));
    }

    public void logoutSession(String token, Long id) {
        accountService.getAccountByToken(token);
        MultiAgent session = getSessionById(id);
        session.setToken(null);
        session.setRefreshToken(null);
        multiAgentRepository.save(session);
    }
    public void logoutAllSessions(String token) {
        Account account = accountService.getAccountByToken(token);
        List<MultiAgent> sessions = multiAgentRepository.findByAccount(account);
        for (MultiAgent session : sessions) {
            session.setToken(null);
            session.setRefreshToken(null);
            multiAgentRepository.save(session);
        }
    }

    public void deleteSession(String token, Long id) {
        accountService.getAccountByToken(token);
        MultiAgent session = getSessionById(id);
        session.setIsActive(false);
        multiAgentRepository.save(session);
    }
}
