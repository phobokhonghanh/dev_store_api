package dev.dev_store_api.auth.factory.creator;

import dev.dev_store_api.account.model.Account;
import dev.dev_store_api.account.dto.AccountDTO;
import dev.dev_store_api.common.model.type.EProvider;

public interface AuthCreator {
    boolean supports(EProvider provider);
    Account create(AccountDTO dto);
}
