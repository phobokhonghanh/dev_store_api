package dev.dev_store_api.auth.factory;

import dev.dev_store_api.auth.factory.creator.AuthCreator;
import dev.dev_store_api.account.model.Account;
import dev.dev_store_api.account.dto.AccountDTO;
import dev.dev_store_api.common.model.type.EProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthFactory {

    private final List<AuthCreator> creators;

    public Account createAccount(AccountDTO dto, EProvider provider) {
        return creators.stream()
                .filter(c -> c.supports(provider))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported provider: " + provider))
                .create(dto);
    }
}

