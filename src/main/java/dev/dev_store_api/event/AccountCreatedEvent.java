package dev.dev_store_api.event;

import dev.dev_store_api.model.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountCreatedEvent {
    private final Account account;

    public AccountCreatedEvent(Account account) {
        this.account = account;
    }
}