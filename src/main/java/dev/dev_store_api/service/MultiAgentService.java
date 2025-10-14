package dev.dev_store_api.service;

import dev.dev_store_api.libs.utils.GenericMapper;
import dev.dev_store_api.repository.MultiAgentRepository;
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
