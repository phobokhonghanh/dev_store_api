package dev.dev_store_api.factory.creator.auth;

import dev.dev_store_api.model.Account;
import dev.dev_store_api.model.dto.AccountDTO;
import dev.dev_store_api.model.type.EProvider;

public interface AuthCreator {
    boolean supports(EProvider provider);
    Account create(AccountDTO dto);
}
