package dev.dev_store_api.auth.factory.creator;

import dev.dev_store_api.common.util.GenericMapper;
import dev.dev_store_api.account.model.Account;
import dev.dev_store_api.account.dto.AccountDTO;
import dev.dev_store_api.common.model.type.EProvider;
import dev.dev_store_api.common.model.type.EStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FacebookAuthCreator implements AuthCreator{
        private final GenericMapper genericMapper;

        @Override
        public boolean supports(EProvider provider) {
            return provider == EProvider.FACEBOOK;
        }

        @Override
        public Account create(AccountDTO dto) {
            Account account = genericMapper.toEntity(dto, Account.class);
            account.setPassword(null);
            account.setStatus(EStatus.ACTIVE.getValue());
            account.setAuthProvider(EProvider.FACEBOOK);
            account.setOtpCode(null);
            return account;
        }
    }

