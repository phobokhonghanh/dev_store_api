package dev.dev_store_api.account.service;

import dev.dev_store_api.account.model.Account;

public interface AccountLookupService {
    Account getAccountByEmail(String email);

    Account getAccountByToken(String token);
    Account findAccountByIdentifier(String identifier);

}
