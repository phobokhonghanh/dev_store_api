package dev.dev_store_api.factory.creator.auth;

import dev.dev_store_api.libs.utils.GenericMapper;
import dev.dev_store_api.model.Account;
import dev.dev_store_api.model.dto.AccountDTO;
import dev.dev_store_api.model.type.EProvider;
import dev.dev_store_api.model.type.EStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SystemAuthCreator implements AuthCreator {
    private final GenericMapper genericMapper;

    public SystemAuthCreator(GenericMapper genericMapper) {
        this.genericMapper = genericMapper;
    }

    @Override
    public boolean supports(EProvider provider) {
        return provider == EProvider.SYSTEM;
    }

    @Override
    public Account create(AccountDTO dto) {
        Account account = genericMapper.toEntity(dto, Account.class);
        account.setPassword(hashPassword(dto.getPassword()));
        account.setStatus(EStatus.UNACTIVE.getValue());
        account.setAuthProvider(EProvider.SYSTEM);
        account.setOtpCode(generateOtp());
        return account;
    }

    private String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    private String generateOtp() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}

