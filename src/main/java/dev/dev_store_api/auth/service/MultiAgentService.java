package dev.dev_store_api.auth.service;

import dev.dev_store_api.auth.repository.MultiAgentRepository;
import org.springframework.stereotype.Service;

@Service
public class MultiAgentService {
    private final MultiAgentRepository multiAgentRepository;

    public MultiAgentService(MultiAgentRepository multiAgentRepository) {
        this.multiAgentRepository = multiAgentRepository;
    }
    public boolean checkToken(String token) {
        return multiAgentRepository.existsByTokenAndIsActiveTrue(token);
    }
}
